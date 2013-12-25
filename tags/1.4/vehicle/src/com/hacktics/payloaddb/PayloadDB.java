package com.hacktics.payloaddb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.parosproxy.paros.Constant;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PayloadDB {

    private static final String fileDelimiter = System.getProperty("file.separator"); //OS Independent
    private static final File f = new File ("");
    private static final String directory = f.getAbsolutePath();
    public static final String PAYLOAD_DATABASE_BASIC =
    		directory + fileDelimiter + "payload-db" + fileDelimiter;
    
    public static BufferedReader getBufferedReader(String filename,URL resourceLocation) throws IOException {
    	BufferedReader buf = null;
			
			// 06/03/2013 alex - multicase file loading (compatibility with market add-ons) 
			//read prefixes/names file
			File file = getFile(Constant.getZapHome()+ "payload-db/"+filename);
			//check installed by market add-ons
			if (file.exists()) {
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			}
			else {
				//check installed by installer
				file = getFile(PAYLOAD_DATABASE_BASIC+filename);
				if (file.exists()) {
					buf = new BufferedReader(new FileReader(PAYLOAD_DATABASE_BASIC+filename));
				}
				else {
					//standalone - load from resource file
					buf = new BufferedReader(new InputStreamReader(getStream(resourceLocation), "UTF-8"));
				}
			}
			
			return buf;
    }
    
    public static Document getDocumentBuilder(String filename,URL resourceLocation) throws ParserConfigurationException, SAXException, IOException {
    	DocumentBuilderFactory factory;
        DocumentBuilder builder;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
		// 06/03/2013 alex - multicase file loading (compatibility with market add-ons) 
		//read prefixes/names file
		File file = getFile(Constant.getZapHome()+ "payload-db/"+filename);
		//check installed by market add-ons
		if (file.exists()) {
			return builder.parse(file);
		}
		else {
			//check installed by installer
			file = getFile(PAYLOAD_DATABASE_BASIC+filename);
			if (file.exists()) {
				return builder.parse(file);
			}
			else {
				//standalone - load from resource file
				return builder.parse(getStream(resourceLocation));
			}
		}

    }
    public static File getFile(String filename) {
    	return new File(filename);
    }
    
    public static InputStream getStream(URL resourceLocation) throws IOException {
    	return resourceLocation.openStream();
    }
    
}
