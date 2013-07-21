package com.hacktics.viewstate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 
 * @author Alex Mor
 *
 */
public class ViewState {
	private final byte Type_Int16 = 1;
	private final byte Type_Int32 = 2;
	private final byte Type_ArrayList = 22;
	
	
    // Format and Version 
    private final byte ViewState_FormatByte = (byte) 255; 
    private final byte ViewState_Version_Byte = (byte) 0x01;
	private String text = "";
	
	public String getText() {
		return text;
	}
	
	private String eventValidation = "";
	
	public String getEventValidation() {
		return eventValidation;
	}
	
	private void setEventValidation(byte[] eventValidation) {
		this.eventValidation =  new String(Base64.encodeBase64(eventValidation));
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

	private void setViewStateMap(Map<String, Object> viewStateMap) {
		this.viewStateMap = viewStateMap;
	}
	
	private Map<String,Object> eventValidationMap;

	public Map<String, Object> getEventValidationMap() {
		return eventValidationMap;
	}

	private void setEventValidationMap(Map<String, Object> eventValidationMap) {
		this.eventValidationMap = eventValidationMap;
	}
	
	private List<String> IndexedString;

	public ViewState(String response) {
		try {
			this.text = this.getViewState(response);
			this.version = this.getViewStateVersion();
			this.viewStateBytes = this.decode64(this.text);
			
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
	private String getViewState(String response){
		
		//find __VIEWSTATE
		String viewStateB64 = getMatchedRegex(response,"__VIEWSTATE");
		String chunks = getMatchedRegex(response,"__VIEWSTATEFIELDCOUNT");
		String eventValidation = getMatchedRegex(response,"__EVENTVALIDATION");
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
		if (this.text.startsWith("/w")) 
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
        	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        	
        	//check format
        	byte format =(byte) bais.read();
        	
        	if (format==(byte)0xff) {
        		
        		//read version
	        	bais.read();
	        	bais.mark(2);
	        	IndexedString = new ArrayList<String>();
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
        	System.out.println(e.getMessage());
        }
        
        return hashMap;
        
	}
	/**
	 * @param bais byte array with the viewstate
	 * @param IndexedString	a list of already used strings
	 */
	private Map<String,Object> parseByteArray (ByteArrayInputStream bais) {
		
		Map<String,Object> hashMap = new HashMap<String,Object>();
		Map<String,Object> dividerMap = new HashMap<String,Object>();
		List<Object> stringListArray = new ArrayList<Object>();

		try {
				
			
	    	int current_char = bais.read();
	    	int num_elements = 0;
	    	int str_len = 0;
	    	int confirm_byte;
	    	byte[] vs_byte_array;
	    	String vs_string = "";
	    	String Array_Type;
	    	
	    	//check if we have reached the end
	    	if (current_char!=-1) {
	    		//detemine the current byte type
	    		String type = getByteType((byte)current_char);
	    		switch (type) {
		    		case "Pair":
		    			stringListArray.add(parseByteArray(bais));
		    			stringListArray.add(parseByteArray(bais));
		    			hashMap.put(type,stringListArray);
		    			break;
		    		case "Triplet":
		    			stringListArray.add(parseByteArray(bais));
		    			stringListArray.add(parseByteArray(bais));
		    			stringListArray.add(parseByteArray(bais));
		    			hashMap.put(type, stringListArray);
		    			break;
		    		case "ArrayList":
		    			//size of array list
		    			num_elements = bais.read();
		    			//add elements accordingly
		    			for (int i=0;i<num_elements;i++) {
		    				stringListArray.add(parseByteArray(bais));
		    			}
		    			hashMap.put(type, stringListArray);
		    			
		    			//ugly, need to find a more elegant solution
		    			//System.out.println(stringListArray.get(1).toString());
		    			if (stringListArray.get(0).toString().contains(("Visible"))) {
		    				if (stringListArray.get(1).toString().contains(("False"))) {
		    				invisibleControls = true;
		    				}
		    			}
		    			break;
		    		case "Array":
		    			confirm_byte = bais.read(); //should be 43=0x2b (Referce by Type)
		    			if (confirm_byte!=0x2b) {
		    				break;
		    			}
		    			Array_Type = getArrayType((byte)bais.read());
		   
		    			//size of array list
		    			num_elements = bais.read();
		    			
		    			//add elements accordingly	    			
		    			for (int i=0;i<num_elements;i++) {
		    				stringListArray.add(parseByteArray(bais));
		    			}
		    			hashMap.put(type+" of " + Array_Type, stringListArray);
		    			break;
		    		case "SparseArray":
		    			confirm_byte = bais.read(); //should be 0x2b (Referce by Type)
		    			if (confirm_byte!=0x2b) {
		    				break;
		    			}
		    			Array_Type = getArrayType((byte)bais.read());
		    			
		    			//size of array list
		    			num_elements = bais.read();
		    			
		    			//read the next byte - itemcounter
		    			int itemCounter = bais.read();
		    			
		    			//protect from bad input
		    			if (num_elements>itemCounter) {
		    				
		    				
			    			for (int i=0;i<itemCounter;i++) {
			    				int next_item = bais.read();
			    				
				    			if (next_item>=0) {
				    				stringListArray.add(parseByteArray(bais));
				    			}
		    				}
		    			}
		    			hashMap.put(type+" of " + Array_Type, stringListArray);
		    			break;
		    		case "Hashtable":
		    		case "HybridDictionary":
		    			num_elements = getLongNum(bais);
		    			//add elements accordingly
		    			for (int i=0;i<num_elements;i++) {
		    				stringListArray.add(parseByteArray(bais));
		    				stringListArray.add(parseByteArray(bais));
		    				dividerMap.put("Dictionary Entry",stringListArray);
		    			}
		    			hashMap.put(type, dividerMap);
		    			break;
		    		case "Boolean:True":
		    			hashMap.put("Boolean", "True");
		    			break;
		    		case "Boolean:False":
		    			hashMap.put("Boolean", "False");
		    			break;
		    		case "Int16":
		    		case "Int32":
		    			//it's a number, read it
		    			int num = getLongNum(bais);	    			
		    			hashMap.put(type,num);
		    			break;
		    		case "Int32:0":
		    			hashMap.put("Int32","0");
		    			break;
		    		case "Int32:-1":
		    			bais.read(); //0xff
		    			bais.read(); //0xff
		    			bais.read(); //0xff
		    			hashMap.put("Int32","-1");
		    			break;
		    		case "String:\"\"":
		    			hashMap.put("String","\"\"");
		    			break;
		    		case "String":
		    		case "Indexed String":
		    			//it's a string, get its size and then read it
		    			str_len = getLongNum(bais);
		    			
		    			vs_byte_array = new byte[str_len];
		    			bais.read(vs_byte_array);
		    			vs_string = new String(vs_byte_array,"UTF-8");
		    			hashMap.put(type,vs_string);
		    			if (type=="Indexed String") {
			    			//add the string to the IndexedString 
			    			IndexedString.add(vs_string);
		    				}
		    			break;
		    		case "IndexedString Array Value":
		    			//get the array index
		    			int array_element = bais.read();
		    			hashMap.put("Indexed String",IndexedString.get(array_element));
		    			break;
		    		case "Array of Strings":
		    			num_elements = getLongNum(bais);	    
		    			
		    			//add Strings accordingly
		    			for (int i=0;i<num_elements;i++) {
			    			//it's a string, get its size and then read it
			    			str_len = getLongNum(bais);
			    			
			    			vs_byte_array = new byte[str_len];
			    			bais.read(vs_byte_array);
			    			vs_string = new String(vs_byte_array,"UTF-8");
			    			stringListArray.add(vs_string);
		    			}
		    			hashMap.put(type, stringListArray);
		    			break;
		    		case "IntEnum":
		    			confirm_byte = bais.read(); //should be 0x2b (Referce by Type)
		    		case "Null":
		    			//
		    			hashMap.put("Null",null);
		    			break;
		    		default:
		    			System.out.println(type);
	    		}
	        		
	        		
	        		
	        	//move the "caret"
	        	bais.mark(0);
	
	    	}
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
    	return hashMap;
	}
	
	
	private String getByteType (byte vs_byte) {
		switch (vs_byte) {
		case 1:
			return "Int16";
		case 2:
			return "Int32"; //System.Int32
		case 3:
			return "Byte";
		case 4:
			return "Char";
		case 5:
			return "String"; //System.String
		case 6:
			return "DateTime"; //System.DateTime
		case 7:
			return "Double"; //System.Double
		case 8:
			return "Single";
		case 9:
			return "Color";
		case 10:
			return "KnownColor";
		case 11:
			return "IntEnum";
		case 12:
			return "EmptyColor";
		case 15: //0x0f
			return "Pair"; //System.Web.UI.Pair
	    case 16:
	    	return "Triplet"; //System.Web.UI.Triplet
	    case 20:
	    	return "Array";     
	    case 21://0x15
	    	return "Array of Strings";
	    case 22://0x16
	    	return "ArrayList"; //System.Collections.ArrayList
	    case 23:
	    	return "Hashtable";
	    case 24: //0x18
	    	return "HybridDictionary";
	    case 25: //0x18
		      return "Type";
	    case 30: //0x1e
	      /*
	       * IndexedString provides optimization that allows the efficient storing of page state information that contains a repeated string
	       */
	    	return "Indexed String"; //System.Web.UI.IndexedString
	    case 31: //0x1f
	    	return "IndexedString Array Value";
	    case 60://0x3c
	    	return "SparseArray"; //many identical values
	    case 100: // 0x64
	    	return "Null";
	    case 101: // 0x65
	     	return "String:\"\""; //System.String
	    case 102: //0x66
	    	return "Int32:0"; //System.Int32
	    case 103:
	    	return "Boolean:True";
	    case 104:
	    	return "Boolean:False";
	    case 0:
	        return "*** ERROR ***";
		}
		System.out.println(vs_byte);
		return "";
	}
	
	private String getArrayType (byte vs_byte) {
		switch (vs_byte) {
		case 0:
			return "Object";
		case 1:
			return "Int32";
		case 2:
			return "String";
		case 3:
			return "Boolean";
		}
		return "";
	}
	
	private int getLongNum(ByteArrayInputStream bais) {
		int num = 0;
		
		try {	
			
			num = bais.read();
			
			//if 0xff than it represents -1
			//if (num==255) {
			//num=bais.read(); //0xff
			//bais.read(); //0xff
			//bais.read(); //0xff
			//return -1;
			//}
			
			//hopefully no more than int (5 bytes)
			if (num>=128) {
				List<String> numList = new ArrayList<String>();
				numList.add(String.valueOf(num));
				
				//read the long number
				while (num>=128) {
					num = bais.read();
					numList.add(String.valueOf(num));
				}
				
				num = 0;
				
				//now build the number
				for (int i=0;i<numList.size();i++) {
					//last num? don't take the 128 off
					if ((i+1)==numList.size()) {
						num = num + (Integer.parseInt(numList.get(i)))*(int)Math.pow(128,i);
					}
					else {
						num = num + (Integer.parseInt(numList.get(i))-128)*(int)Math.pow(128,i);
					}
				}
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		return num;
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
			// TODO Auto-generated catch block
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
		hashMap.put("Int32",hash);
		try {
			((ArrayList<Map<String, Object>>)eventValidationMap.get("ArrayList")).add(hashMap);
			UpdateEventValidation();
		}
		catch (ClassCastException ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void UpdateEventValidation() {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(ViewState_FormatByte);
		baos.write(ViewState_Version_Byte);
		Serialize(baos,eventValidationMap);
		
		/*
		//add event validation MAC - recalculate
		if (eventValidationSigned) {
			try {
				baos.write(Hex.decodeHex(evmac.toCharArray()));
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		eventValidationBytes = baos.toByteArray();
		setEventValidation(eventValidationBytes);
		

	}
	
	private void Serialize(ByteArrayOutputStream baos,Map<String, Object> object) {
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
	private void SerializeArrayList(ByteArrayOutputStream baos,Object value) {
		for (Object arrayObject : ((ArrayList<?>)value)) {
			switch (arrayObject.getClass().getName()) {
				case "java.util.HashMap":
					Serialize(baos,(Map<String, Object>) arrayObject);
					break;
			}
			
		}
	}
	private void SerializeValue(ByteArrayOutputStream baos,Map.Entry<String, Object> entryObject) {
		try {
			switch (entryObject.getKey()) {
			case "Int32":
				baos.write(Type_Int32);
				WriteLongInteger(baos,Integer.parseInt(entryObject.getValue().toString()));
				break;
			case "ArrayList":
				baos.write(Type_ArrayList);
				baos.write(((ArrayList<?>)entryObject.getValue()).size());
				SerializeArrayList(baos,entryObject.getValue());
				break;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void WriteLongInteger(ByteArrayOutputStream baos,int value) {
		//convert to unsigned int
		//http://www.darksleep.com/player/JavaAndUnsignedTypes.html
		int firstByte = (0xFF000000 & value) >> 24;
        int secondByte = (0x00FF0000 & value) >> 16;
        int thirdByte = (0x0000FF00 & value) >> 8;
        int fourthByte = (0x000000FF & value);
    
		long UnsignedInt  = ((long) (firstByte << 24
		                | secondByte << 16
	                        | thirdByte << 8
	                        | fourthByte))
	                       & 0xFFFFFFFFL;
		
		while (UnsignedInt >= 0x80) {
			baos.write(((byte)(UnsignedInt | 0x80)));
			UnsignedInt >>= 7; 
        }
		baos.write((byte)UnsignedInt); 
	}
}


