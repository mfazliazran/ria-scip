package com.hacktics.vehicle.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMessage;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.hacktics.vehicle.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.zaproxy.zap.network.HttpRequestBody;

public class BlindEnumerateControls extends JFrame implements ActionListener {
	Vector<String> vectorResultsColumns;
	DefaultTableModel resultsModel = new DefaultTableModel();
	JTable tableResults;
	private String stringControlName = "";
	private DotNetPage dotNetPage;
	JList<String> listEvents;
	JButton buttonEventRequest = new JButton("Run Event");
	JList<String> listVisibleControls;
	JList<String> listOtherControls;
	JScrollPane JtableControlsScrollable;
	
	// CSRF Tokens
	
	private static ArrayList<String> CSRFtokenNames = new ArrayList<String>();
	
	private static final long serialVersionUID = 2145387227181332965L;
	//private JTextArea jTextAreaControlConsole;
	private JTextField jTextPrefix;
	
	private JLabel jLabelURL = new JLabel("URL:");
	private JLabel jLabelControlsFound = new JLabel("");
	private static int numFoundControls;
	private JTextField jTextURL;
	private List<String> foundControlsArray = Collections.synchronizedList(new ArrayList<String>()); 
	public List<String> getFoundControls() {
		return foundControlsArray;
	}
	
	private String baseRequest = "";
	private String baseControlName = "";
	private HttpMessage enumMessage;
	private String URL;
	private boolean isChanceForReflection = false;
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	@SuppressWarnings("unused")
	private Proxy proxy;   
	private ArrayList<String> prefixControlArray = new Dictionary("prefix.txt",false);
	private ArrayList<String> controlNamesArray = new Dictionary("names.txt",true);
	
	JPanel contentPane = new JPanel(new GridBagLayout());
	
	public BlindEnumerateControls(HttpMessage msg,String BaseRequest,String BaseControlName,Proxy proxy) {
		
		baseRequest = BaseRequest;
		baseControlName = BaseControlName;
		try {
			enumMessage = msg.cloneRequest(); 
			setURL(enumMessage.getRequestHeader().getURI().getURI());
		} catch (URIException e) {
			e.printStackTrace();
		}
		this.proxy = proxy;
		numFoundControls = 0;
		//Set up the content pane.
		
		//Create and add the components.
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	initComponents();
		    	
		    }
		});
		
		
	}
	
	private void initComponents()
    {
		// Init CSRF Tokens
		CSRFtokenNames.add("__VIEWSTATE");
		CSRFtokenNames.add("__VIEWSTATEENCRYPTED");
		CSRFtokenNames.add("__EVENTTARGET");
		CSRFtokenNames.add("__EVENTARGUMENT");
		CSRFtokenNames.add("__EVENTVALIDATION");
		CSRFtokenNames.add("__LASTFOCUS");
		CSRFtokenNames.add("__ASYNCPOST");
		CSRFtokenNames.add("__VSTATE");
		CSRFtokenNames.add("__PREVIOUSPAGE");
		CSRFtokenNames.add("__REQUESTDIGEST");
		CSRFtokenNames.add("__RequestVerificationToken");
		CSRFtokenNames.add("org.apache.struts.taglib.html.TOKEN");
		CSRFtokenNames.add("_javax.faces.ViewState");
		CSRFtokenNames.add("CFTOKEN");
		CSRFtokenNames.add("anticsrf");
		CSRFtokenNames.add("CSRFToken");
		CSRFtokenNames.add("ViewStateUserKey");
		CSRFtokenNames.add("jsessionid");
		CSRFtokenNames.add("sessionid");
		CSRFtokenNames.add("PHPSESSID");
		CSRFtokenNames.add("PHPSESSIONID");
		CSRFtokenNames.add("ASP.NET_SessionId");
		CSRFtokenNames.add("ASPSESSIONID");
		CSRFtokenNames.add("cfid");
		CSRFtokenNames.add("FORMCRED");
		CSRFtokenNames.add("SMSESSION");
		CSRFtokenNames.add("BV_EngineID");
		CSRFtokenNames.add("BV_SESSIONID");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10,10,10,10);
		contentPane.add(jLabelURL, gbc);
		
		jTextURL = new JTextField(getURL());
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5,10,5,5);
		contentPane.add(jTextURL, gbc);
		
		JLabel jLabelPrefix = new JLabel("Prefix:");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10,10,10,10);
		contentPane.add(jLabelPrefix, gbc);
		
		jTextPrefix = new JTextField(baseControlName);
		jTextPrefix.setPreferredSize(new Dimension(150,25));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(10,10,10,5);
		contentPane.add(jTextPrefix, gbc);
		
		JButton jButtonRun = new JButton("Run");
		gbc.gridx = 2;
		gbc.gridy = 1;
		
		contentPane.add(jButtonRun, gbc);
		jButtonRun.setActionCommand("Run Enumeration");
		jButtonRun.addActionListener(this);
		
		//create empty table
		vectorResultsColumns = new Vector<String>(Arrays.asList("Request", "Response Length", "Similarity"));
		resultsModel.setDataVector(new Vector<Vector<String>>(), vectorResultsColumns);
		tableResults = new JTable(resultsModel);
		tableResults.getColumnModel().getColumn(0).setPreferredWidth(70);
		tableResults.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableResults.getColumnModel().getColumn(2).setPreferredWidth(70);
		tableResults.getTableHeader().setResizingAllowed(false);
		//define selection operations
		ListSelectionModel cellSelectionModel = tableResults.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

	    cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent e) {    
		    	  if ((tableResults.getSelectedRow()>=0) && (tableResults.getSelectedColumn()>=0)) {
		    		  stringControlName = tableResults.getValueAt(tableResults.getSelectedRow(),0).toString();
		    		  DefaultListModel<String> listModelEvents = new DefaultListModel<String>();
				  		List<String> selectedControlEvents = dotNetPage.getControlEvents().get(stringControlName);
				  		if (selectedControlEvents!=null) {
							for (String controlName : selectedControlEvents) {
								listModelEvents.addElement(controlName);
							}
				  		}
						listEvents.setModel(listModelEvents);
						buttonEventRequest.setEnabled(true);
		    	  }
		    	  listVisibleControls.removeSelectionInterval(listVisibleControls.getSelectedIndex(),listVisibleControls.getSelectedIndex());
		    	  listOtherControls.removeSelectionInterval(listOtherControls.getSelectedIndex(),listOtherControls.getSelectedIndex());
		      }

		    });

		
		JtableControlsScrollable = new JScrollPane(tableResults);
		JtableControlsScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.gridy = 3;
		gbc.insets = new Insets(10,0,0,0);
		contentPane.add(JtableControlsScrollable,gbc);

		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.gridy = 4;
		contentPane.add(jLabelControlsFound, gbc);
		
		JButton jButtonApply = new JButton("Apply");
		gbc.insets = new Insets(10,0,10,10);
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.gridy = 4;
		contentPane.add(jButtonApply, gbc);
		jButtonApply.setActionCommand("Apply");
		jButtonApply.addActionListener(this);
		
		//create window
		add(contentPane);
		setTitle("Blind Control Enumeration");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400,400);
		
		setLocationRelativeTo(null);
		
		pack();
		setVisible(true);
		
		setResizable(true);
    }

	@Override
	public void actionPerformed(ActionEvent ev) {
		switch (ev.getActionCommand()) {
		case "Apply":
			//http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
			WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			dispose();
			break;
		case "Run Enumeration":
			PerformEnumeration();
		}
		
	}
	
	private void PerformEnumeration() {
		jLabelControlsFound.setText("In Progress, Controls Found: "+ numFoundControls);
		final String stringControlNameConvention = jTextPrefix.getText();
		
		//allow enumerating another page and adding its controls
		if (jTextURL.getText() != null) {
			try {
				setURL(jTextURL.getText());
				enumMessage.getRequestHeader().setURI(new URI(getURL(),false));
				enumMessage.getRequestHeader().setMethod("post");
		    	enumMessage.getRequestHeader().addHeader("Content-Type", "application/x-www-form-urlencoded");
			} catch (URIException | NullPointerException e) {
				e.printStackTrace();
			}
		}
		else
			return;
		
		(new Thread()
		{	
			public void run() {	
				String baseStringResponseAfterRemoval = "";
				String demeStringResponseAfterRemoval = "";
				// Treating the Base Request
				// ------------------------------------------------------------------------------------------
				// Sending the request and reciving the response
				String baseRequestBody = baseRequest;
		    	enumMessage.setRequestBody(baseRequestBody.getBytes());
		    	enumMessage.getRequestHeader().setContentLength(baseRequestBody.length());
		    	ZAP.send(enumMessage);
		    	String baseStringResponse = enumMessage.getResponseBody().toString(); 
		    	// removing only timestamp
		    	baseStringResponse = RemoveTimeStamps(baseStringResponse);
		    	// Removing CSRF tokens from the original request's response
		    	baseStringResponseAfterRemoval = RemoveCSRFTokens(baseStringResponse);
		    	
		    	// Checking if there is a chance for a reflection of a new parameter
		    	// Sending a deme request (with aaaa=bbbb parameter)
		    	String demeRequestBody = baseRequest+"&aaaa=bbbb";
		    	enumMessage.setRequestBody(demeRequestBody.getBytes());
		    	enumMessage.getRequestHeader().setContentLength(demeRequestBody.length());
		    	ZAP.send(enumMessage);
		    	String demeStringResponse = enumMessage.getResponseBody().toString();
		    	// removing only timestamp
		    	demeStringResponse = RemoveTimeStamps(demeStringResponse);
		    	// Removing CSRF tokens from the deme request's response 
		    	demeStringResponseAfterRemoval = RemoveCSRFTokens(demeStringResponse);
		    	
		    	// checking if the responses are similar without removing the tokens (because it takes time)
		    	// if the original & deme responses are similar after removing time stamps --> there is no chance for reflection
		    	if (GetResponsesSimilarity.getResponsesSimilarity(baseStringResponse, demeStringResponse) == 100){
		    		isChanceForReflection = false;
				}
		    	else // remove Tokens and test similarity again
		    	{
			    	// if the original & deme responses are not equal after removing tokens and time stamps --> there is a chance for reflection
			    	if (GetResponsesSimilarity.getResponsesSimilarity(baseStringResponseAfterRemoval, demeStringResponseAfterRemoval) != 100){
			    		isChanceForReflection = true;
					}
		    	} 	
		    	
	    		resultsModel.addRow(new String[]{"Base request", String.valueOf(baseStringResponseAfterRemoval.length()), ""});
				
				for (final String strPrefix : prefixControlArray) {
					for (final String strControlName : controlNamesArray)
					{   
						// Treating the target Request
						// ------------------------------------------------------------------------------------------
				
				    	String targetRequestBody = baseRequest+"&"+stringControlNameConvention+strPrefix+strControlName+"=Button";
				    	enumMessage.setRequestBody(targetRequestBody.getBytes());
				    	enumMessage.getRequestHeader().setContentLength(targetRequestBody.length());
				    	ZAP.send(enumMessage);
				    	String targetStringResponse = enumMessage.getResponseBody().toString();
				    	
				    	// Removing the irrelevant data from the response: CSRF tokens and Time stamps 
				    	/*
				    	if ((strPrefix+strControlName).equals("Box1")){
				    		System.out.println("After Removal Base response:" + baseStringResponse.toString() + "\r\n");
					    	// removing only timestamp
				    		targetStringResponse = RemoveTimeStamps(targetStringResponse);
				    		System.out.println("After Removal Box1 Response:\r\n"+ targetStringResponse.toString() + "\r\n");
				    	}
				    	else
				    	{
				    		targetStringResponse = RemoveTimeStamps(targetStringResponse);
				    	}
				    	*/
				    	
			    		targetStringResponse = RemoveTimeStamps(targetStringResponse);
				    	
				    	// Computing the responses' similarity after removing only timestamp
				    	// ------------------------------------------------------------------------------------------
				    	
				    	int similarity = GetResponsesSimilarity.getResponsesSimilarity(baseStringResponse, targetStringResponse);
				    	// if the responses are similar --> there is no control, do not test reflection
				    	// only if the responses are not similar, remove tokens and test similarity again
				    	if (similarity != 100){
				    		// remove tokens
					    	String targetStringResponseAfterRemoval = RemoveCSRFTokens(targetStringResponse);
					    	similarity = GetResponsesSimilarity.getResponsesSimilarity(baseStringResponseAfterRemoval, targetStringResponseAfterRemoval);
					    	// if the responses are still different --> test reflection
					    	if (similarity != 100){
					    		// test reflection
					    		if (isChanceForReflection){
					    			// remove reflection

					    			// TODO: reflection!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
					    			targetStringResponseAfterRemoval = RemoveReflections(targetStringResponseAfterRemoval, stringControlNameConvention+strPrefix+strControlName, "Button", demeStringResponseAfterRemoval, "aaaa", "bbbb");
					    			// TODO: reflection!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
			
					    			// test if the responses are similar
					    			similarity = GetResponsesSimilarity.getResponsesSimilarity(baseStringResponseAfterRemoval, targetStringResponseAfterRemoval);
							    	// if the responses are still different --> the control exists
							    	if (similarity != 100){
						    			String newControl = stringControlNameConvention + strPrefix + strControlName; 
						    			AddNewControl(newControl, targetStringResponseAfterRemoval.length(), similarity);
							    	}				    			
					    		}
					    		else // the responses are differnet --> the control exists
					    		{
					    			String newControl = stringControlNameConvention + strPrefix + strControlName; 
					    			AddNewControl(newControl, targetStringResponseAfterRemoval.length(), similarity);
					    		}
					    	}
			    		}
				    }		
				}
				jLabelControlsFound.setText("Finished! Controls Found: "+ numFoundControls);
				
			}
		}).start();	
	}

	
	private void AddNewControl(String newControl, int responseLength, int similarity){
		// Adding the found commands to the found list		    		
     	//check duplicates
		boolean foundDuplicate = false;
		for (String controlName : foundControlsArray) {
			if (controlName.compareToIgnoreCase(newControl)==0) {
				foundDuplicate = true;
			}
		}
		if (!foundDuplicate) {
			// increase counter for number of found controls
			// Print the control into table
	    	resultsModel.addRow(new String[]{newControl, String.valueOf(responseLength), String.valueOf(similarity)});
			numFoundControls++;
			jLabelControlsFound.setText("In Progress, Controls Found: "+ numFoundControls);
			
		}
		foundControlsArray.add(newControl);
	}
	
	private String RemoveCSRFTokens(String response){
		String cleanResponse;
		// Removing CSRF Tokens
		List<String> removeTagList = CSRFtokenNames;
		ArrayList<UnRelevantTag> diffTags = new ArrayList<UnRelevantTag>();
		for (String csrfToken : removeTagList) {
			diffTags.add(new UnRelevantTag("<input", csrfToken, ">"));
			diffTags.add(new UnRelevantTag("\\?", csrfToken, ">"));
			diffTags.add(new UnRelevantTag("&", csrfToken, "/>"));
		}
		List<UnRelevantTag> diffTagList = diffTags;
		cleanResponse = removeUnRelevantData(response, diffTagList, removeTagList);	
		return cleanResponse;
	}

	// Removes: Response Body UnRelavent Tags, Line Breakes
    public static String removeUnRelevantData(String originalResponseStr, List<UnRelevantTag> unRelevantTags,List<String> removed_tags) {              
        for (UnRelevantTag unRlvTag : unRelevantTags)
        {
            String startStr = unRlvTag.startTagStr;
            String endStr = unRlvTag.endTagStr;
            if (endStr=="" || endStr==null) 
            	endStr = "\'?\"?&?>"; //this is yet tested
            String searchStr = unRlvTag.middleTagStr;
            
            startStr = Pattern.quote(startStr);
            endStr = Pattern.quote(endStr);
            searchStr = Pattern.quote(searchStr);
            
            String paternGreater = "(" + startStr + ")(.*?)(" + endStr +")";
            Pattern myPatternGreater = Pattern.compile(paternGreater, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher myMatcherGreater = myPatternGreater.matcher(originalResponseStr);

            String paternInner = "(.*)(" + searchStr + ")(.*)";
            Pattern myPatternInner = Pattern.compile(paternInner, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher myMatcherInner = null;

            String currentGreaterStr = "";
            String s1 = "";
            String s2 = "";
            
            while (myMatcherGreater.find())
            {
                currentGreaterStr = originalResponseStr.substring(myMatcherGreater.start(), myMatcherGreater.end());

                myMatcherInner = myPatternInner.matcher(currentGreaterStr);

                if (myMatcherInner.find())
                // It's a match, need to delete this greater part
                {   
                	//save removed tag content
                	if (removed_tags!=null)
                	removed_tags.add(originalResponseStr.substring(myMatcherGreater.start(),myMatcherGreater.end()));
                	
                	//remove the tag
                	s1 = originalResponseStr.substring(0, myMatcherGreater.start());
                    s2 = originalResponseStr.substring(myMatcherGreater.end(), originalResponseStr.length());
                    originalResponseStr = s1 + s2;
                    myMatcherGreater = myPatternGreater.matcher(originalResponseStr);
                }       
            }
        }
              
        return originalResponseStr;
    } 

	private String RemoveReflections(String targetResponse, String paramName, String paramValue, String demeResponse, String demeParamName, String demeParamValue){
		// Removing Reflections
		
		// PARAM NAME
		// while there are more reflections
		while (demeResponse.indexOf(demeParamName) != -1){
			int reflectionStartIndex = demeResponse.indexOf(demeParamName);
			// if the value in the index location is indeed the parameter Name --> remove it
			if ((targetResponse.substring(reflectionStartIndex, reflectionStartIndex+paramName.length())).equals(paramName))
			{
				targetResponse = targetResponse.substring(0, reflectionStartIndex) + targetResponse.substring(reflectionStartIndex+paramName.length(), targetResponse.length());
				demeResponse = demeResponse.substring(0, reflectionStartIndex) + demeResponse.substring(reflectionStartIndex+demeParamName.length(), demeResponse.length());
			}			
		}

		// PARAM VALUE
		// while there are more reflections
		while (demeResponse.indexOf(demeParamValue) != -1){
			int reflectionStartIndex = demeResponse.indexOf(demeParamValue);
			// if the value in the index location is indeed the parameter Name --> remove it
			if ((targetResponse.substring(reflectionStartIndex, reflectionStartIndex+paramValue.length())).equals(paramValue))
			{
				targetResponse = targetResponse.substring(0, reflectionStartIndex) + targetResponse.substring(reflectionStartIndex+paramValue.length(), targetResponse.length());
				demeResponse = demeResponse.substring(0, reflectionStartIndex) + demeResponse.substring(reflectionStartIndex+demeParamValue.length(), demeResponse.length());
			}			
		}		
					
		return targetResponse;
	}
	
	private String RemoveTimeStamps(String response){
		String cleanResponse;
		// Removing Time stamps
		// [0-9]{1,4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}		
		cleanResponse = Pattern.compile("(\\d{1,2}:\\d{1,2}:\\d{1,2})").matcher(response).replaceAll("");
		return cleanResponse;
	}
	
	
}
