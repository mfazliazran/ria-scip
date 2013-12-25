package com.hacktics.viewstate;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.hacktics.payloaddb.PayloadDB;

/**
 * 
 * @author Alex Mor
 *
 */
public class ViewState {
	// Format and Version 
	private final byte ViewState_FormatByte = (byte) 255; 
	private final byte ViewState_Version_Byte = (byte) 0x01;

	private String eventValidation = "";
	private String viewState = "";
	private List<String> eventValidationArray = new ArrayList<String>();

	public String getEventValidation() {
		return eventValidation;
	}

	private void setEventValidation(byte[] eventValidation) {
		this.eventValidation =  new String(Base64.encodeBase64(eventValidation));
	}
	
	public List<String> getEventValidationArray() {
		return eventValidationArray;
	}

	private void setEventValidationArray(List<String> eventValidationArray) {
		this.eventValidationArray =  eventValidationArray;
	}
	
	private void addControlToEventValidationArray(String hash) {
		this.eventValidationArray.add(hash);
	}

	public String getViewStateText() {
		return viewState;
	}

	private void setViewStateText(byte[] viewState) {
		this.viewState =  new String(Base64.encodeBase64(viewState));
	}
	private boolean encrypted = false;

	public boolean isEncrypted() {
		return encrypted;
	}

	private boolean macProtected = false;

	public boolean isViewStateMACSigned() {
		return macProtected;
	}

	private String mac = "";

	public String getMAC() {
		return mac;
	}

	private String evmac = "";

	public String getEVMAC() {
		return evmac;
	}

	private boolean split = false;

	public boolean isSplit() {
		return split;
	}
	
	private boolean splitNoCount = false;

	public boolean isSplitNoCount() {
		return splitNoCount;
	}

	private boolean invisibleControls = false;

	public boolean isInvisibleControls() {
		return invisibleControls;
	}

	private int version;

	public int getVersion(){
		return version;
	}

	private boolean eventValidationExists = false;

	public boolean isEventValidationExists() {
		return eventValidationExists;
	}

	private boolean eventValidationSigned = false;

	public boolean isEventValidationSigned() {
		return eventValidationSigned;
	}

	private byte[] viewStateBytes;
	private byte[] eventValidationBytes;

	private Map<String,Object> viewStateMap;

	public Map<String, Object> getViewStateMap() {
		return viewStateMap;
	}

	public void setViewStateMap(Map<String, Object> viewStateMap) {
		if (this.viewStateMap != null) {
			UpdateViewState(viewStateMap);
		}
		this.viewStateMap = viewStateMap;
		this.setViewStateText(this.viewStateBytes);

	}

	private Map<String,Object> eventValidationMap;

	public Map<String, Object> getEventValidationMap() {
		return eventValidationMap;
	}

	private void setEventValidationMap(Map<String, Object> eventValidationMap) {
		this.eventValidationMap = eventValidationMap;
	}

	private List<String> IndexedString;
	private List<String> RefTypes;

	public ViewState(String response) {
		try {
			this.viewState = this.getViewStateBase64(response);
			this.version = this.getViewStateVersion();
			this.viewStateBytes = this.decode64(this.viewState);

			if (this.isEventValidationExists()) {
				eventValidationBytes = this.decode64(this.eventValidation);
				
			}


			//if it's not encrypted, and successfully decoded from base64, decode the ViewState into hashmap
			if ((!this.encrypted) && (this.viewStateBytes.length>0)) {
				setViewStateMap(this.decode(this.viewStateBytes,true));
				//System.out.println(getViewStateMap());
				//also decode the Event Validation
				if (isEventValidationExists()) {
					setEventValidationMap(this.decode(this.eventValidationBytes,false));
					buildEventValidationArray();
					//System.out.println(getEventValidationMap());
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	/**
	 * @param response raw response body
	 * @return a base64 string, representing the ViewState field 
	 */
	private String getViewStateBase64(String response){

		//find __VIEWSTATE
		String viewStateB64 = getMatchedRegex(response,"__VIEWSTATE");
		String chunks = getMatchedRegex(response,"__VIEWSTATEFIELDCOUNT");
		String eventValidation = getMatchedRegex(response,"__EVENTVALIDATION");
		
		//new form of viewstate splitting
		if (chunks=="") {
			chunks = getMatchedRegex(response,"__VIEWSTATE0");
			if (chunks!="") {
				int chunksPlus = Integer.parseInt(chunks) +1 ;
				chunks = String.valueOf(chunksPlus);
				this.splitNoCount = true;
			}
		}
		if (chunks!="") {
			for (int i=1;i<Integer.parseInt(chunks);i++) {
				//now find __VIEWSTATE(i)
				viewStateB64 += getMatchedRegex(response,"__VIEWSTATE" + i);
			}
			this.split = true;
		}

		//find __VIEWSTATEENCRYPTED
		if (getMatchedRegex(response,"__VIEWSTATEENCRYPTED")!="") {
			this.encrypted = true;
		}

		//find __EVENTVALIDATION
		if (eventValidation!="") {
			this.eventValidationExists = true;
			this.eventValidation = eventValidation;
		}

		return viewStateB64;
	}

	/**
	 * @param Response	raw response body
	 * @param Regex	regular expression term to search for in the response
	 */
	private String getMatchedRegex(String Response,String Regex) {
		String matchedRegex = "";
		String regex = "<input type=\"hidden\" name=\"" + Regex+ "\" id=\"" + Regex + "\" value=\"(.*?)\" />";
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(Response);

		if (matcher.find()) {
			try {
				matchedRegex = matcher.group(1);
			}
			catch (Exception e){
				System.out.println(e.toString());
			}
		}

		return matchedRegex;
	}

	private int getViewStateVersion() {
		if (this.getViewStateText().startsWith("/w")) 
			return 2;
		else
			return 1;
	}

	/**
	 * @param base64 a base64 encoded string
	 */
	private byte[] decode64(String base64) {
		if (!Base64.isArrayByteBase64(base64.getBytes())) {
			return null;
		}
		return Base64.decodeBase64(base64);
	}
	/**
	 * decode a base64 string to a object
	 * @param base64 a encoded string by ViewState.encode method
	 */
	private Map<String,Object> decode(byte[] bytes,boolean decodeViewstate) {
		if (bytes==null) {
			throw new NullPointerException("Byte array is null");
		}

		Map<String,Object> hashMap = new HashMap<String,Object>();

		try {
			DeserializeDataInputStream bais = new DeserializeDataInputStream(bytes);

			//check format
			byte format =(byte) bais.read();

			if (format==(byte)0xff) {

				//read version
				bais.read();
				bais.mark(2);
				IndexedString = new ArrayList<String>();
				RefTypes = new ArrayList<String>(Arrays.asList("Type_Object","Type_Int32","Type_String","Type_Boolean"));

				//attempt to parse viewstate from byte array
				hashMap =  parseByteArray(bais);

				//get the MAC in the end
				if ((bais.available()>=20) && (this.version==2)) {

					//creat an array of the remaining unread bytes
					byte[] macBytes = new byte[bais.available()];
					bais.read(macBytes);
					String decodedMAC = Hex.encodeHexString(macBytes).toUpperCase();
					if (decodeViewstate) {
						this.macProtected = true;
						this.mac = decodedMAC;
					}
					else
					{
						this.eventValidationSigned = true;
						this.evmac = decodedMAC;

					}


				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hashMap;

	}
	/**
	 * @param bais byte array with the viewstate
	 * @param IndexedString	a list of already used strings
	 */
	private Map<String,Object> parseByteArray (DeserializeDataInputStream bais) {

		Map<String,Object> hashMap = new HashMap<String,Object>();
		Map<String,Object> dividerMap = new HashMap<String,Object>();
		Map<String,Object> nullMap = new HashMap<String,Object>();
		List<Object> stringListArray = new ArrayList<Object>();
		List<Object> mapHelperArray = new ArrayList<Object>();
		MicrosoftTypes microsoftType;

		try {


			int current_char = bais.read();
			int num_elements = 0;
			String vs_string = "";
			String Type_Ref;

			//create null hash map
			nullMap.put("Type_Null", "null");

			//check if we have reached the end
			if (current_char!=-1) {
				//detemine the current byte type
				microsoftType = MicrosoftTypes.fromInt(current_char);
				String type = microsoftType.toString();
				

				switch (microsoftType) {
				case Type_Pair:
					stringListArray.add(parseByteArray(bais));
					stringListArray.add(parseByteArray(bais));
					hashMap.put(type,stringListArray);
					break;
				case Type_Triplet:
					stringListArray.add(parseByteArray(bais));
					stringListArray.add(parseByteArray(bais));
					stringListArray.add(parseByteArray(bais));
					hashMap.put(type, stringListArray);
					break;
				case Type_ArrayList:
					//size of array list
					num_elements = bais.read();
					//add elements accordingly
					for (int i=0;i<num_elements;i++) {
						stringListArray.add(parseByteArray(bais));
					}
					hashMap.put(type, stringListArray);

					//ugly, need to find a more elegant solution
					//System.out.println(stringListArray.get(1).toString());
					if (stringListArray.size()>1) {
						if (stringListArray.get(0).toString().contains(("Visible"))) {
							if (stringListArray.get(1).toString().contains(("False"))) {
								invisibleControls = true;
							}
						}
					}
					break;
				case Type_Array:
					Type_Ref = DeserializeTypeRef(bais);

					//size of array list
					num_elements = bais.read();

					//add elements accordingly	    			
					for (int i=0;i<num_elements;i++) {
						stringListArray.add(parseByteArray(bais));
					}
					dividerMap.put(Type_Ref,stringListArray);
					mapHelperArray.add(dividerMap);
					hashMap.put(type,mapHelperArray);
					break;
				case Type_SparseArray:
					Type_Ref = DeserializeTypeRef(bais);

					//size of array list
					num_elements = bais.read();

					//read the next byte - itemcounter
					int itemCounter = bais.read();

					//protect from bad input
					if (num_elements>itemCounter) {

						//create null array
						for (int i=0;i<num_elements;i++) {
							stringListArray.add(nullMap);
						}

						for (int i=0;i<itemCounter;i++) {
							int next_item = bais.read();

							if (next_item>=0) {
								stringListArray.set(next_item,parseByteArray(bais));
							}
						}
					}

					dividerMap.put(Type_Ref,stringListArray);
					mapHelperArray.add(dividerMap);
					hashMap.put(type,mapHelperArray);
					break;
				case Type_Hashtable:
				case Type_HybridDictionary:
					num_elements = bais.Read7BitEncodedInt();
					//add elements accordingly
					for (int i=0;i<num_elements;i++) {
						dividerMap = new HashMap<String,Object>();
						stringListArray = new ArrayList<Object>();
						stringListArray.add(parseByteArray(bais));
						stringListArray.add(parseByteArray(bais));
						dividerMap.put("Type_Dictionary Entry",stringListArray);
						mapHelperArray.add(dividerMap);
					}
					//mapHelperArray.add(dividerMap);
					hashMap.put(type, mapHelperArray);
					break;
				case Type_True:
					hashMap.put(type, "True");
					break;
				case Type_False:
					hashMap.put(type, "False");
					break;
				case Type_Int16:
				case Type_Int32:
				case Type_Int64:
					//it's a number, read it	    			
					hashMap.put(type,bais.readInt());
					break;
				case Type_ZeroInt32:
					hashMap.put(type,"0");
					break;
				/*
				case "Int32:-1":
					bais.read(); //0xff
					bais.read(); //0xff
					bais.read(); //0xff
					hashMap.put("Int32","-1");
					break;*/
				case Type_EmptyString:
					hashMap.put(type,"\"\"");
					break;
				case Type_KnownColor:
					int color = bais.Read7BitEncodedInt();
					hashMap.put(type,com.hacktics.viewstate.Color.fromInt(color));
					break;
				case Type_String:
				case Type_IndexedStringAdd:
					vs_string = bais.readString();				
					if (microsoftType == MicrosoftTypes.Type_IndexedStringAdd) {
						hashMap.put(MicrosoftTypes.Type_IndexedString.toString(),vs_string);
						//add the string to the IndexedString 
						IndexedString.add(vs_string);
					}
					else {
						hashMap.put(type,vs_string);
					}
					break;
				case Type_IndexedString:
					//get the array index
					int array_element = bais.read();
					if (IndexedString.size()>array_element) {
						hashMap.put(type,IndexedString.get(array_element));
					}
					break;
				case Type_StringArray:
					num_elements = bais.Read7BitEncodedInt();	    

					//add Strings accordingly
					for (int i=0;i<num_elements;i++) {
						//it's a string, get its size and then read it
						vs_string = bais.readString();
						//stringListArray.add(vs_string);
						Map<String,Object> stringHashMap = new HashMap<String,Object>();
						stringHashMap.put(MicrosoftTypes.Type_String.toString(),vs_string);
						stringListArray.add(stringHashMap);
					}
					hashMap.put(type, stringListArray);
					break;
				case Type_IntEnum:
					dividerMap.put("Type_Enum_Type",DeserializeTypeRef(bais));
					mapHelperArray.add(dividerMap);
					dividerMap = new HashMap<String,Object>();
					dividerMap.put("Type_Enum_Value",bais.readInt());
					mapHelperArray.add(dividerMap);
					hashMap.put(type, mapHelperArray);
					break;
				case Type_Type:
					//is this a known type?
					hashMap.put(type,DeserializeTypeRef(bais));
					break;
				case Type_Null:
					//
					hashMap.put(type,null);
					break;
				default:
					System.out.println(type);
				}



				//move the "caret"
				bais.mark(0);

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return hashMap;
	}

	private String DeserializeTypeRef (DeserializeDataInputStream bais) {
		MicrosoftTypes type_Ref = MicrosoftTypes.fromInt(bais.read());
		switch (type_Ref) {
		case TypeRefAdd:
		case TypeRefAddLocal:
			//this is a new type
			try {
				String TypeRefAdd = bais.readString();
				RefTypes.add(TypeRefAdd);
				return TypeRefAdd;
			} catch (IOException e) {
				e.printStackTrace();
			} 
			break;
		case TypeRef:
			try {
				return RefTypes.get(bais.readInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return "";
	}


	/**
	 * @param uniqueId the control's name
	 * @return control's hash value
	 * Original C# .Net file - StringUtil.cs
	 */
	public static int GetUniqueIdHash(String uniqueId) {
		//uniqueId is case sensitive!!
		int hash1 = (5381 << 16) + 5381;
		int hash2 = hash1;

		if (uniqueId.length()==0) {
			return 0;
		}
		byte[] charUnique = null;
		try {
			charUnique = uniqueId.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteBuffer buffer = ByteBuffer.wrap(charUnique);
		//.NET little endian by default
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int len = uniqueId.length();

		try {
			while (len > 0)
			{
				//one 2 byte char is left
				if (buffer.limit()-buffer.position()<=2) {
					hash1 = ((hash1 << 5) + hash1 + (hash1 >> 27)) ^ buffer.getChar();	
				}
				else {
					hash1 = ((hash1 << 5) + hash1 + (hash1 >> 27)) ^ buffer.getInt();
				}

				if (len <= 2)
				{
					break;
				}
				if (len==3) {
					hash2 = ((hash2 << 5) + hash2 + (hash2 >> 27)) ^ buffer.getChar();
				}
				else {
					hash2 = ((hash2 << 5) + hash2 + (hash2 >> 27)) ^ buffer.getInt();	
				}

				len -= 4;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return hash1 + (hash2 * 1566083941);

	}

	@SuppressWarnings("unchecked")
	public void AddEvent(String uniqueId,String argument) {
		int hash = (GetUniqueIdHash(uniqueId) ^ GetUniqueIdHash(argument));		
		Map<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put(MicrosoftTypes.Type_Int32.toString(),hash);
		try {
			((ArrayList<Map<String, Object>>)eventValidationMap.get(MicrosoftTypes.Type_ArrayList.toString())).add(hashMap);
			UpdateEventValidation();
			addControlToEventValidationArray(String.valueOf(hash));
			
		}
		catch (ClassCastException ex) {
			ex.printStackTrace();
		}

	}
	
	private void buildEventValidationArray() {
		for (Map<String, Object> mapHash : ((ArrayList<Map<String, Object>>)eventValidationMap.get(MicrosoftTypes.Type_ArrayList.toString()))) {
			addControlToEventValidationArray(mapHash.get(MicrosoftTypes.Type_Int32.toString()).toString());
		}
	}

	private void UpdateEventValidation() {

		SerializeDataOutputStream baos = new SerializeDataOutputStream();
		baos.write(ViewState_FormatByte);
		baos.write(ViewState_Version_Byte);
		Serialize(baos,eventValidationMap);

		eventValidationBytes = baos.toByteArray();
		setEventValidation(eventValidationBytes);

	}

	private void UpdateViewState(Map<String, Object> viewStateMap) {

		SerializeDataOutputStream baos = new SerializeDataOutputStream();
		IndexedString = new ArrayList<String>();
		RefTypes = new ArrayList<String>(Arrays.asList("Type_Object","Type_Int32","Type_String","Type_Boolean"));
		baos.write(ViewState_FormatByte);
		baos.write(ViewState_Version_Byte);
		Serialize(baos,viewStateMap);

		viewStateBytes = baos.toByteArray();
		setViewStateText(viewStateBytes);


		UpdateEventValidationMap();

	}

	@SuppressWarnings("unchecked")
	private void UpdateEventValidationMap() {

		if (!isEventValidationExists()) 
			return;
		
		//replace current viewstate hash value in event validation
		ArrayList<Map<String, Object>> evArrayList = (ArrayList<Map<String, Object>>) this.eventValidationMap.get(MicrosoftTypes.Type_ArrayList.toString());

		Map<String, Object> evViewStateHashMap = (Map<String, Object>)evArrayList.get(0);

		//update viewstate hash
		int viewstateHash = GetUniqueIdHash(this.getViewStateText());

		//overwrite the hashmap, this also updates the entire Event Validation object.
		evViewStateHashMap.put(MicrosoftTypes.Type_Int32.toString(), viewstateHash);

	}

	private void Serialize(SerializeDataOutputStream baos,Map<String, Object> object) {
		switch (object.getClass().getName()) {
		case "java.util.HashMap":
			for (Map.Entry<String, Object> hashMapObject : object.entrySet()) {
				SerializeValue(baos,hashMapObject);
				/*switch (hashMapObject.getKey()) {
				case "ArrayList":
					SerializeArrayList(baos,hashMapObject.getValue());
					break;
				case "Int32":
					break;
				}*/
				//Serialize(baos,(HashMap<String, Object>) hashMapObject.getValue());				
			}
			break;
			/*
		case "java.util.HashMap$Entry":
			for (Map.Entry<String, Object> hashMapObject : object.entrySet()) {
				Serialize(baos,(HashMap<String, Object>) hashMapObject.getValue());
			}
			break;*/
		}
	}


	@SuppressWarnings("unchecked")
	private void SerializeArrayList(SerializeDataOutputStream baos,Object value) {
		for (Object arrayObject : ((ArrayList<?>)value)) {
			switch (arrayObject.getClass().getName()) {
			case "java.util.HashMap":
				Serialize(baos,(Map<String, Object>) arrayObject);
				break;
			}

		}
	}
	@SuppressWarnings("unchecked")
	private void SerializeValue(SerializeDataOutputStream baos,Entry<String, Object> entryObject) {
		MicrosoftTypes microsoftType = MicrosoftTypes.valueOf(entryObject.getKey());
		String value = entryObject.getValue().toString();
		Map<String, Object>  arrayMap;
		try {
			switch (microsoftType) {
			case Type_Int16:
			case Type_Int32:
			case Type_Int64:
				baos.write(microsoftType.getType());
				baos.write7BitEncodedInt(Integer.parseInt(value));
				break;
			case Type_ZeroInt32:
				baos.write(MicrosoftTypes.Type_ZeroInt32.getType());
				break;
			case Type_ArrayList:
				baos.write(MicrosoftTypes.Type_ArrayList.getType());				
				baos.write7BitEncodedInt(((ArrayList<?>)entryObject.getValue()).size());
				SerializeArrayList(baos,entryObject.getValue());
				break;
			case Type_Pair:
				baos.write(MicrosoftTypes.Type_Pair.getType());
				Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(0));
				Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(1));
				break;
			case Type_Triplet:
				baos.write(MicrosoftTypes.Type_Triplet.getType());
				Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(0));
				Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(1));
				Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(2));
				break;
			case Type_IntEnum:
				//there are still problems with this enum!!!
				baos.write(MicrosoftTypes.Type_IntEnum.getType());
				for (Map<String, Object> internalArrayObject : ((ArrayList<Map<String, Object>>)entryObject.getValue())) {
					if (internalArrayObject.containsKey("Type_Enum_Type")) {
						//write enum type
						value = internalArrayObject.get("Type_Enum_Type").toString();
						SeriazlizeTypeRef(baos,value);
					}
					else {
						//write enum value
						value = internalArrayObject.get("Type_Enum_Value").toString();
						baos.write7BitEncodedInt(Integer.parseInt(value));
					}
				}	
				break;
			case Type_String:
				baos.write(MicrosoftTypes.Type_String.getType());
				baos.write7BitEncodedInt(value.length());
				baos.write(value.getBytes("UTF-8"));
				break;
			case Type_EmptyString:
				baos.write(MicrosoftTypes.Type_EmptyString.getType());
				break;
			case Type_KnownColor:
				baos.write(MicrosoftTypes.Type_KnownColor.getType());
				baos.write7BitEncodedInt(com.hacktics.viewstate.Color.valueOf(value).getColor());
				break;
			case Type_IndexedString:
				int array_element = IndexedString.indexOf(value);
				if (array_element>=0) {
					//already indexed the string
					baos.write(MicrosoftTypes.Type_IndexedString.getType());
					baos.write7BitEncodedInt(array_element);
				}
				else {
					//index string add
					baos.write(MicrosoftTypes.Type_IndexedStringAdd.getType());
					baos.write7BitEncodedInt(value.length());
					baos.write(value.getBytes("UTF-8"));
					IndexedString.add(value);
				}
				break;
			case Type_StringArray:
				baos.write(MicrosoftTypes.Type_StringArray.getType());
				baos.write7BitEncodedInt(((ArrayList<?>)entryObject.getValue()).size());

				//very complex design.. maybe improve this some day:
				//  HashMap
				//	Key = Array of Strings (String)
				//	Value = ArrayList (type)
				//	0 = HashMap
				//  Key = String (String)
				//  Value = [the real value] (String)
				//	1 = HashMap
				//  Key = String (String)
				//  Value = [the real value] (String)
				//loop through the list values
				for (Map<String, Object> internalArrayObject : ((ArrayList<Map<String, Object>>)entryObject.getValue())) {
					value = internalArrayObject.get("Type_String").toString();
					baos.write7BitEncodedInt(value.length());
					baos.write(value.getBytes("UTF-8"));
				}
				break;
			case Type_Type:
				baos.write(MicrosoftTypes.Type_Type.getType());
				SeriazlizeTypeRef(baos,value);
				break;
			case Type_Array:
			case Type_SparseArray:
				if (microsoftType==MicrosoftTypes.Type_Array)
					baos.write(MicrosoftTypes.Type_Array.getType());
				if (microsoftType==MicrosoftTypes.Type_SparseArray)
					baos.write(MicrosoftTypes.Type_SparseArray.getType());

				arrayMap = ((Map<String, Object>)((ArrayList<Map<String, Object>>)entryObject.getValue()).get(0));

				for (Map.Entry<String, Object> hashMapObject : arrayMap.entrySet()) {
					//write the type of the sparse array
					SeriazlizeTypeRef(baos,hashMapObject.getKey());
					//write the value
					List<Map<String, Object>> array = (ArrayList<Map<String, Object>>)hashMapObject.getValue();
					//write the array size
					baos.write7BitEncodedInt(array.size());

					if (microsoftType==MicrosoftTypes.Type_SparseArray) {
						//write the number of array values
						int numOfValues = 0;
						for (int i=0;i<array.size();i++) {
							Map<String, Object> sparseItem = array.get(i);
							if (!sparseItem.containsKey(MicrosoftTypes.Type_Null.toString())) {
								numOfValues++;
							}
						}
						baos.write7BitEncodedInt(numOfValues);
					}



					//loop thorugh the array
					for (int i=0;i<array.size();i++) {
						Map<String, Object> arrayItem = array.get(i);
						if (!arrayItem.containsKey(MicrosoftTypes.Type_Null.toString())) {
							if (microsoftType==MicrosoftTypes.Type_SparseArray)
								baos.write7BitEncodedInt(i);
							Serialize(baos,arrayItem);
						}
					}

				}
				break;

			case Type_Null:
				baos.write(MicrosoftTypes.Type_Null.getType());
				break;

			case Type_True:
				baos.write(MicrosoftTypes.Type_True.getType());				
				break;
			case Type_False:
				baos.write(MicrosoftTypes.Type_False.getType());
				break;
			case Type_Hashtable:
			case Type_HybridDictionary:
				if (microsoftType==MicrosoftTypes.Type_Hashtable)
					baos.write(MicrosoftTypes.Type_Hashtable.getType());
				if (microsoftType==MicrosoftTypes.Type_HybridDictionary)
					baos.write(MicrosoftTypes.Type_HybridDictionary.getType());

				//number of elements in the Hashtable/HybridDictionary
				ArrayList<Map<String, Object>> dictionaryArray = (ArrayList<Map<String, Object>>)entryObject.getValue();
				baos.write7BitEncodedInt(dictionaryArray.size());
				
				//get internal object
				for (Map<String, Object> dictionaryMap : dictionaryArray )
					{
					for (Map.Entry<String, Object> hashMapObject : dictionaryMap.entrySet()) {		
						//write key and value
						Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)hashMapObject.getValue()).get(0));
						Serialize(baos,(Map<String, Object>)((ArrayList<Map<String, Object>>)hashMapObject.getValue()).get(1));
					}
				}





				break;

			default:
				System.out.println(entryObject.getKey());
			}
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Serialization error in viewstate object","Error",JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void SeriazlizeTypeRef (SerializeDataOutputStream baos,String type) {
		try {
			if (RefTypes.contains(type)) {
				baos.write(MicrosoftTypes.TypeRef.getType());
			}
			else {
				baos.write(MicrosoftTypes.TypeRefAdd.getType());
				baos.writeString(type);
			}

			baos.write7BitEncodedInt(RefTypes.indexOf(type));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class DeserializeDataInputStream extends DeserializationReader {
	//private final InputStream inputStream;

	public DeserializeDataInputStream(byte[] buf) {
		super(buf);
	}

}

class SerializeDataOutputStream extends SerializationWriter {


}


