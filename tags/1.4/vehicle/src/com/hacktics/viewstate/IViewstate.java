package com.hacktics.viewstate;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


public interface IViewstate {
	public String Serialize(Map<String, Object> objectMap);
	public void Serialize(OutputStream outputStream, Map<String, Object> objectMap);
	public void SerializeValue(SerializationWriter writer, Map<String, Object> value);
	public void SerializeIndexedString(SerializationWriter writer, String indexedString);
	public void SerializeType(SerializationWriter writer, String type);
	
	
	public Map<String, Object> Deserialize(String inputString);
	public Map<String, Object> Deserialize(InputStream inputStream);
	public Map<String, Object> DeserializeValue(DeserializationReader reader);
	public Map<String, Object> DeserializeIndexedString(DeserializationReader reader, IType type);
	public String DeserializeType(DeserializationReader reader);
	
	
	
}
