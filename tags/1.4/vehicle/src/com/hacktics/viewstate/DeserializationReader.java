package com.hacktics.viewstate;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;


public abstract class DeserializationReader extends ByteArrayInputStream{
	public DeserializationReader(byte[] buf) {
		super(buf);
	}
	public String readString() throws IOException {
		return readUTF();
	}
	
	
	/**
	 * Read out an Int32.
	 * @return the decoded integer (Int32).
	 * @throws IOException
	 */
	public int readInt() throws IOException {
		return Read7BitEncodedInt();
	}
	
	//public abstract char readChar() throws IOException;
	//public abstract long readLong() throws IOException;
	//public abstract float readSingle() throws IOException;
	//public abstract double readDouble() throws IOException;
	
	/**
	 * A UTF-8 string starts with the length.
	 * @return the decoded string.
	 * @throws IOException
	 */
	public String readUTF() throws IOException {
		 int len = Read7BitEncodedInt();
		 if (len < 0)
	            throw new IndexOutOfBoundsException();
		 
	     byte[] bytearr = new byte[len];
	     //read the UTF-8 string
	     this.read(bytearr);
		 return new String(bytearr,"UTF-8");
	}
	
	/**
	 * Read out an Int32, 7 bits at a time.  
	 * When the high bit of the byte is on, continue reading more bytes.
	 * @return the decoded integer (Int32).
	 * @throws IOException
	 */
	//http://stackoverflow.com/questions/5022956/space-efficient-long-representation
	public int Read7BitEncodedInt() throws IOException {
		int shift = 0;
		int b;
		int value = 0;
		while((b = this.read()) >= 0) {
			value += (b & 0x7f) << shift;
			shift += 7;
			if ((b & 0x80) == 0) return value;
		}
		throw new EOFException("Unable to read 7Bit encoded Int32");
	}
}
