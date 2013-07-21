package com.hacktics.scip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.Constant;


/**
 * @author alex.mor
 *
 */
public class Dictionary extends ArrayList<String>{
	       
	/**
	 * 
	 */
	private static final long serialVersionUID = 3742514889297491945L;

    private static final String fileDelimiter = System.getProperty("file.separator"); //OS Independent
    private static final File f = new File ("");
    private static final String directory = f.getAbsolutePath();
    public static final String PAYLOAD_DATABASE_BASIC =
    		directory + fileDelimiter + "payload-db" + fileDelimiter;
    
	public Dictionary (String filename,boolean numebrs){
		String value;	
		BufferedReader buf = null;
		try {
			
			//read prefixes/names file
			File file = new File(Constant.getZapHome()+ "payload-db/"+filename);
			//check installed by market add-ons
			if (file.exists()) {
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			}
			else {
				//check installed by installer
				file = new File(PAYLOAD_DATABASE_BASIC+filename);
				if (file.exists()) {
					buf = new BufferedReader(new FileReader(PAYLOAD_DATABASE_BASIC+filename));
				}
				else {
					//load from resource file
					URL fileURL = getClass().getResource("resource/fuzz/"+filename);
					buf = new BufferedReader(new InputStreamReader(fileURL.openStream(), "UTF-8"));
				}
			}
			
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
