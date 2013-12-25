package com.hacktics.vehicle;

import java.io.BufferedReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import com.hacktics.payloaddb.PayloadDB;


/**
 * @author alex.mor
 *
 */
public class Dictionary extends ArrayList<String>{
	       
	/**
	 * 
	 */
	private static final long serialVersionUID = 3742514889297491945L;
    
	public Dictionary (String filename,boolean numebrs){
		String value;	
		BufferedReader buf = null;
		
		try {
			buf = PayloadDB.getBufferedReader(filename, getClass().getResource("resource/fuzz/"+filename));
			
			//BufferedReader 
			while ((value = buf.readLine()) != null) {
				//fuzz value
				/*	value
				 * 	Value
				 * 	VALUE
				 * 	_value
				 * 	_Value
				 * 	_VALUE
				 * 	-value
				 * 	-Value
				 * 	-VALUE
				 */
				if (value=="") continue; 
				add(value);
				add(StringUtils.capitalize(value));
				/*
				add(value.toUpperCase());
				add("_" + value);
				add("_" + StringUtils.capitalize(value));
				add("_" + value.toUpperCase());
				add("-" + value);
				add("-" + StringUtils.capitalize(value));
				add("-" + value.toUpperCase());
				*/
				
			}
			buf.close();
			if (numebrs) {
				for (int i=0;i<=10;i++)
				{
					add(String.valueOf(i));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,e.getMessage());
		} 
	}
	
	public void saveListToFile(String filename) {
		
	}
	
}
