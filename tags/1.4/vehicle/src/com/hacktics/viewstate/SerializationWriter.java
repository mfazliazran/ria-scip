package com.hacktics.viewstate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public abstract class SerializationWriter extends ByteArrayOutputStream{
	
	public void writeString(String s) throws IOException {
		writeUTF(s);
	}
	public void writeUTF(String s) throws IOException {
		//write string length
		write7BitEncodedInt(s.length());
		this.write(s.getBytes("UTF-8"));
	}
			
	public void write7BitEncodedInt(int v) throws IOException {
			//convert to unsigned int
			//http://www.darksleep.com/player/JavaAndUnsignedTypes.html
			int firstByte = (0xFF000000 & v) >> 24;
	        int secondByte = (0x00FF0000 & v) >> 16;
	        int thirdByte = (0x0000FF00 & v) >> 8;
	        int fourthByte = (0x000000FF & v);
	    
			long UnsignedInt  = ((long) (firstByte << 24
			                | secondByte << 16
		                        | thirdByte << 8
		                        | fourthByte))
		                       & 0xFFFFFFFFL;
			
			while (UnsignedInt >= 0x80) {
				this.write(((byte)(UnsignedInt | 0x80)));
				UnsignedInt >>= 7; 
	        }
			this.write((byte)UnsignedInt); 
		
	}
	
}
