package com.hacktics.scip.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import com.hacktics.scip.SCIP;
import com.hacktics.scip.DotNetPage;
import com.hacktics.scip.ZAP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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

public class SCIPView implements ActionListener {
	
	private DotNetPage dotNetPage;
	
	//Info Components
	JFrame frame = new JFrame(APP_NAME);
	JPanel infoPanel = new JPanel();
	JPanel controlsPanel = new JPanel(new GridBagLayout());
	JCheckBox jCheckBoxViewStateFound = new ReadOnlyCheckBox("ViewState.");
	JCheckBox jCheckBoxViewStateSigned = new ReadOnlyCheckBox("ViewState Signed (MAC found).");
	JCheckBox jCheckBoxViewStateEncrypted = new ReadOnlyCheckBox("ViewState Encrypted");
	JCheckBox jCheckBoxEventValidationFound = new ReadOnlyCheckBox("Event Validation.");
	JCheckBox jCheckBoxEventValidationSigned = new ReadOnlyCheckBox("Event Validation Signed (MAC found).");
	
	//page analysis
	JLabel invisibleControlsFound = new JLabel("Invisible Controls Found in ViewState.");
	JLabel disabledControlsFound = new JLabel("Disabled Controls Found.");
	JLabel commentedControlsFound = new JLabel("Commented Controls Found.");
	
	//Control Components
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	JTable tableResults;
	DefaultTableModel resultsModel = new DefaultTableModel();
	//Vector<Vector<String>> vectorResults = new Vector<Vector<String>>();
	Vector<String> vectorResultsColumns;
	JList<String> listVisibleControls;
	JList<String> listOtherControls;
	JList<String> listEvents;
	JLabel labelURL = new JLabel("URL:");
	JLabel labelVisibleControls = new JLabel("Visible:");
	JLabel labelOtherControls = new JLabel("Commented or Disabled:");
	JLabel labelResults = new JLabel("Enumeration Results:");
	JLabel labelEvents = new JLabel("Events:");
	JButton buttonEventRequest = new JButton("Run Event");
	JButton jButtonGetURL = new JButton("Get");
	JButton jButtonEnumControls = new JButton("Enumerate Controls");
	JButton jButtonBlindEnumControls = new JButton("Blind Control Enumeration");
	JButton jButtonParseControlsFromHistory = new JButton("Load Controls From History");
	JButton jButtonParseControlsFromURL = new JButton("Add Controls From URL");
	JButton jButtonAddControl = new JButton("Add");
	JTextField jTextFieldURL;
	
	// Request URL
	private String stringURL = "";
	private HttpMessage orgMessage;
	private String proxyHost = ZAP.getProxyIP();
	private int proxyPort = ZAP.getProxyPort();
	private Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	
	// Current selected control name
	private String stringControlName = "";
	
	//constants
	private static final String APP_NAME = "SCIP - RIA Event Enumerator";
	private static final String VERSION = "1.1";
	private static final String ABOUT = "Developer: Alex Mor\nResearchers: Niv Sela & Shay Chen\nVersion:" + VERSION;	
	public SCIPView(HttpMessage hm) {
		
		//keep message for use in resend
		orgMessage = hm;
		if (hm!=null) {
			try {
				stringURL = hm.getRequestHeader().getURI().getURI();
			} catch (URIException e) {
				e.printStackTrace();
			}
		}
		//this.dotNetPage = dotNetPage;
		//stringURL = this.dotNetPage.getUrl();
		//jLabelURL = new JLabel("URL: " + this.dotNetPage.getUrl());
		
	}
	
	public void createShowGUI() {
		//Create and set up the window.
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		//Set up the content pane.
		Container contentPane = frame.getContentPane();
		
		//Create and add the components.
		initComponents(contentPane,frame);
		        
		//Display the window.
	
		frame.pack();
		frame.setSize(new Dimension(720, 680));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(true);
		
		
		if (orgMessage!=null) {
			actionPerformed(new ActionEvent(this, 1, "Get URL"));
		}
	}
	
	private void initComponents(Container contentPane,final JFrame frame)
    {
		//insert URL to field
		jTextFieldURL = new JTextField(stringURL);
		
		//first add page info (viewstate, eventvalidation...)
		infoPanel.setBorder(BorderFactory.createTitledBorder("Page Analysis"));
		infoPanel.setLayout(new GridBagLayout());  // one column
	
		//initialize page analysis labels
		invisibleControlsFound.setIcon(new ImageIcon(getClass().getResource("/resource/icon/16/075.png")));
		commentedControlsFound.setIcon(new ImageIcon(getClass().getResource("/resource/icon/16/074.png")));
		disabledControlsFound.setIcon(new ImageIcon(getClass().getResource("/resource/icon/16/074.png")));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0,0,0,20);
		gbc.gridy = 0;
		gbc.gridx = 0;
		infoPanel.add(jCheckBoxViewStateFound,gbc);
		gbc.gridy = 1;
		infoPanel.add(jCheckBoxViewStateSigned,gbc);
		gbc.gridy = 2;
		infoPanel.add(jCheckBoxViewStateEncrypted,gbc);
		gbc.gridy = 3;
		infoPanel.add(jCheckBoxEventValidationFound,gbc);
		gbc.gridy = 4;
		infoPanel.add(jCheckBoxEventValidationSigned,gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,0);
		infoPanel.add(invisibleControlsFound,gbc);
		gbc.gridy = 1;
		infoPanel.add(disabledControlsFound,gbc);
		gbc.gridy = 2;
		infoPanel.add(commentedControlsFound,gbc);
		
		//disable all checkboxes
		jCheckBoxViewStateFound.setEnabled(false);
		jCheckBoxViewStateSigned.setEnabled(false);
		jCheckBoxViewStateEncrypted.setEnabled(false);
		jCheckBoxEventValidationFound.setEnabled(false);
		jCheckBoxEventValidationSigned.setEnabled(false);
		invisibleControlsFound.setEnabled(false);
		disabledControlsFound.setEnabled(false);
		commentedControlsFound.setEnabled(false);
		
		//create empty table
		vectorResultsColumns = new Vector<String>(Arrays.asList("Control Name", "URL", "Hidden"));
		resultsModel.setDataVector(new Vector<Vector<String>>(), vectorResultsColumns);
		tableResults = new JTable(resultsModel);
		tableResults.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableResults.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableResults.getColumnModel().getColumn(2).setPreferredWidth(50);
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
		
		
		JScrollPane tableControlsScrollable = new JScrollPane(tableResults);
		
		//create empty lists
		listVisibleControls = new JList<String>(new String[]{""});
		listVisibleControls.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent evt) {	    	  
		    	  if (((JList<?>) evt.getSource()).getSelectedIndex()>-1) {
						stringControlName = ((JList<?>) evt.getSource()).getSelectedValue().toString();
				  		DefaultListModel<String> listModelEvents = new DefaultListModel<String>();
				  		List<String> selectedControlEvents = dotNetPage.getControlEvents().get( ((JList<?>) evt.getSource()).getSelectedValue());
				  		if (selectedControlEvents!=null) {
							for (String controlName : selectedControlEvents) {
								listModelEvents.addElement(controlName);
							}
				  		}
						listEvents.setModel(listModelEvents);
						buttonEventRequest.setEnabled(true);
						listOtherControls.removeSelectionInterval(listOtherControls.getSelectedIndex(),listOtherControls.getSelectedIndex());
						tableResults.clearSelection();
			      }
		      }
		    });
		listVisibleControls.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listVisibleControls.setLayoutOrientation(JList.VERTICAL);
	    listVisibleControls.setVisibleRowCount(6);
	    JScrollPane listControlsScrollable = new JScrollPane(listVisibleControls);
	    controlsPanel.setBorder(BorderFactory.createTitledBorder("Page Controls"));
	    listOtherControls = new JList<String>(new String[]{""});
	    listOtherControls.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent evt) {	    	  
		    	  if (((JList<?>) evt.getSource()).getSelectedIndex()>-1) {
						stringControlName = ((JList<?>) evt.getSource()).getSelectedValue().toString();
				  		DefaultListModel<String> listModelEvents = new DefaultListModel<String>();
				  		List<String> selectedControlEvents = dotNetPage.getControlEvents().get( ((JList<?>) evt.getSource()).getSelectedValue());
				  		if (selectedControlEvents!=null) {
							for (String controlName : selectedControlEvents) {
								listModelEvents.addElement(controlName);
							}
				  		}
						listEvents.setModel(listModelEvents);
						buttonEventRequest.setEnabled(true);
						listVisibleControls.removeSelectionInterval(listVisibleControls.getSelectedIndex(),listVisibleControls.getSelectedIndex());
						tableResults.clearSelection();
			      }
		      }
		    });
	    listOtherControls.setVisibleRowCount(6);
	    listOtherControls.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    listOtherControls.setLayoutOrientation(JList.VERTICAL);
	    JScrollPane listOtherControlsScrollable = new JScrollPane(listOtherControls);
	    

		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		controlsPanel.add(labelVisibleControls,gbc);
		gbc.gridx = 4;
		gbc.ipadx = 75;
		gbc.insets = new Insets(0,5,0,0);
		controlsPanel.add(labelOtherControls,gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.ipady = 100;
		gbc.insets = new Insets(0,0,0,0);
	    controlsPanel.add(listControlsScrollable,gbc);
	    gbc.gridwidth = 1;
	    gbc.gridx = 4;
	    gbc.insets = new Insets(0,5,0,0);
	    controlsPanel.add(listOtherControlsScrollable,gbc);
	    gbc.insets = new Insets(0,0,0,0);
	    gbc.ipady = 0;
	    gbc.ipadx = 20;
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    controlsPanel.add(jButtonAddControl,gbc);
	    gbc.gridx = 1;
	    controlsPanel.add(jButtonEnumControls,gbc);
	    gbc.gridx = 2;
		controlsPanel.add(jButtonBlindEnumControls,gbc);

    
	    
		listEvents = new JList<String>(new String[]{""});		
        listEvents.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listEvents.setLayoutOrientation(JList.VERTICAL);
        listEvents.setVisibleRowCount(5);
        listEvents.setPreferredSize(new Dimension(150,50));
        //listEvents.setFixedCellWidth(150);
        //listEvents.setFixedCellHeight(20);
        
        JScrollPane listEventsScrollable = new JScrollPane(listEvents);
        
        //initialize buttons
        buttonEventRequest.setActionCommand("Run Event");
        buttonEventRequest.addActionListener(this);
        buttonEventRequest.setEnabled(false);
        
        jButtonGetURL.setActionCommand("Get URL");
        jButtonGetURL.addActionListener(this);
        
        jButtonAddControl.setActionCommand("Add");
        jButtonAddControl.addActionListener(this);
        jButtonAddControl.setEnabled(false);
        
        jButtonEnumControls.setActionCommand("Enumerate Controls");
        jButtonEnumControls.addActionListener(this);
        jButtonEnumControls.setEnabled(false);
        jButtonEnumControls.setLocation(1, 2);
        
        jButtonBlindEnumControls.setActionCommand("Enumerate Blind Controls");
        jButtonBlindEnumControls.addActionListener(this);
        jButtonBlindEnumControls.setEnabled(false);
        
        jButtonParseControlsFromHistory.setActionCommand("Controls From History");
        jButtonParseControlsFromHistory.addActionListener(this);
        jButtonParseControlsFromHistory.setEnabled(false);
        
        jButtonParseControlsFromURL.setActionCommand("Controls From URL");
        jButtonParseControlsFromURL.addActionListener(this);
        jButtonParseControlsFromURL.setEnabled(false);
        
        //create menus
        JMenuBar menuBar;
        JMenu menuHelp,menuOptions;
        JMenuItem jMenuItemAbout, jMenuItemProxy, jMenuItemExit;
        JMenuItem jMenuItemPrefixEdit,jMenuItemControlEdit;
        
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build menus.
        menuOptions = new JMenu("Options");
        menuHelp = new JMenu("Help");
        
        jMenuItemAbout = new JMenuItem("About");
        jMenuItemProxy = new JMenuItem("Proxy");
        jMenuItemExit = new JMenuItem("Exit");
        jMenuItemPrefixEdit = new JMenuItem("Edit Prefix File");
        jMenuItemControlEdit = new JMenuItem("Edit Control Names File");
       
        jMenuItemAbout.addActionListener(this);
        menuHelp.add(jMenuItemAbout);
        
        jMenuItemProxy.addActionListener(this);
        jMenuItemPrefixEdit.addActionListener(this);
        jMenuItemPrefixEdit.setEnabled(false);
        jMenuItemControlEdit.addActionListener(this);
        jMenuItemControlEdit.setEnabled(false);
        jMenuItemExit.addActionListener(this);
        
        //menuOptions.add(jMenuItemProxy);
        menuOptions.addSeparator();
        menuOptions.add(jMenuItemPrefixEdit);
        menuOptions.add(jMenuItemControlEdit);
        menuOptions.addSeparator();
        menuOptions.add(jMenuItemExit);
        
        menuBar.add(menuOptions);
        menuBar.add(menuHelp);
        
        //set the menu bar
        frame.setJMenuBar(menuBar);
        
        //begin component layout
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        							.addComponent(labelURL)
        							.addComponent(jTextFieldURL)
        							.addComponent(jButtonGetURL))
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        							.addComponent(infoPanel))			
        							//seperate panel from button
        							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,GroupLayout.DEFAULT_SIZE, 29)
        							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        							.addComponent(jButtonParseControlsFromHistory)
	        							.addComponent(jButtonParseControlsFromURL))
	        						//seperate buttons from right dock
	        						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, 35))
					   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(controlsPanel))
				       .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				    		   .addGroup(layout.createSequentialGroup()
				    				   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					    				   .addComponent(tableControlsScrollable)
					    				   .addComponent(labelResults))
				    				   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							        	    	.addComponent(labelEvents)
							        	    	.addComponent(listEventsScrollable)
							        	    	.addComponent(buttonEventRequest)))))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, 30)
        	);
        
        //set the same size
        //layout.linkSize(SwingConstants.HORIZONTAL, jButtonBlindEnumControls);
        layout.linkSize(SwingConstants.HORIZONTAL, jButtonParseControlsFromHistory, jButtonParseControlsFromURL);
        layout.setVerticalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        				.addGroup(layout.createSequentialGroup()
	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        						.addComponent(labelURL)
	        						.addComponent(jTextFieldURL)
	        						.addComponent(jButtonGetURL))
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(infoPanel)
									.addGroup(layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,GroupLayout.DEFAULT_SIZE,40)
										.addComponent(jButtonParseControlsFromHistory)			
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,GroupLayout.DEFAULT_SIZE,15)
										.addComponent(jButtonParseControlsFromURL)))))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	    .addComponent(controlsPanel))       	    	
	        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	    .addComponent(labelResults)
				        	    .addComponent(labelEvents))
			        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	    .addComponent(tableControlsScrollable)
				        	    .addComponent(listEventsScrollable))
			        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	    		.addComponent(buttonEventRequest))))
        	);



    }

	private String getRequest(String controlName) {
			//String stringHost = URLReader.getHost(jTextFieldURL.getText());
			String stringEventValidation = dotNetPage.getViewState().getEventValidation();
			String stringViewState = dotNetPage.getViewState().getText();
			String stringRequest = "";
			try {
				stringRequest = "__EVENTTARGET="+controlName+"&__EVENTARGUMENT=&__EVENTVALIDATION="+URLEncoder.encode(stringEventValidation,"UTF-8") +
										"&__VIEWSTATE="+URLEncoder.encode(stringViewState,"UTF-8");
				if (dotNetPage.getViewState().isEncrypted()) {
					stringRequest += "&__VIEWSTATEENCRYPTED=";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return stringRequest;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String URL = jTextFieldURL.getText();
		
	
		switch (e.getActionCommand()) {
			case "Run Event":
				if (dotNetPage!=null) {
					HttpMessage eventMessage;
					String stringRequest = getRequest(stringControlName);
					if (orgMessage!=null) {
						eventMessage = orgMessage.cloneRequest();
						eventMessage.setRequestBody(new HttpRequestBody(stringRequest));
						eventMessage.getRequestHeader().setMethod("POST");
						eventMessage.getRequestHeader().addHeader("Content-Type", "application/x-www-form-urlencoded");
						ZAP.showZapRepeater(eventMessage);
						//String stringResponse = URLReader.postURLResponse(URL, stringRequest,proxy);
					}
					else
					{
						HttpRequestBody hrb = new HttpRequestBody(stringRequest);
						HttpRequestHeader hrh = null;
						try {
							hrh = new HttpRequestHeader("POST",new URI(URL,false), "HTTP/1.1");
						} catch (URIException | HttpMalformedHeaderException e1) {
							e1.printStackTrace();
						}
						
						eventMessage = new HttpMessage(hrh,hrb);
						eventMessage.getRequestHeader().addHeader("Content-Type", "application/x-www-form-urlencoded");
						ZAP.showZapRepeater(eventMessage);
						
					}
				}
				break;
			case "Get URL":
				if ((URL.startsWith("http://")) | (URL.startsWith("https://"))) {					
					String response = "";
					
					//reset controls
					//disable all checkboxes
					jCheckBoxViewStateFound.setEnabled(false);
					jCheckBoxViewStateSigned.setEnabled(false);
					jCheckBoxViewStateEncrypted.setEnabled(false);
					jCheckBoxEventValidationFound.setEnabled(false);
					jCheckBoxEventValidationSigned.setEnabled(false);
					invisibleControlsFound.setEnabled(false);
					disabledControlsFound.setEnabled(false);
					commentedControlsFound.setEnabled(false);
					
					//hashmap to hold the control-events list (ie: control ctl00 has the following events - onclick,onchage etc)
					HashMap<String,List<String>> controlEvents = new HashMap<String,List<String>>();
					
					try {
						if (orgMessage!=null) {
							if (!URL.equals(orgMessage.getRequestHeader().getURI().getURI())) {
								orgMessage = null;
							}
						}
					} catch (URIException e2) {
						e2.printStackTrace();
					}
					//get page response
					if (orgMessage!=null) {
						response = orgMessage.getResponseBody().toString();

						//if the URL is not yet crawled
						if (response.length()==0) {
							ZAP.send(orgMessage);
							response = orgMessage.getResponseBody().toString();
						}
					}
					else
					{
						try {
							orgMessage = new HttpMessage(new URI(URL,false));
							ZAP.send(orgMessage);
						} catch (URIException | HttpMalformedHeaderException e1) {
							e1.printStackTrace();
						}
						response = orgMessage.getResponseBody().toString();
					}
					if (response.length()>0) {
						
						//add commented and disabled buttons
						List<String> otherControls = SCIP.getComments(response);
						DefaultListModel<String> listModelOtherControls = new DefaultListModel<String>();
						
						for (String otherControl : otherControls) {
							controlEvents.put(otherControl, SCIP.getControlEvents(response,otherControl));
							listModelOtherControls.addElement(otherControl);
							commentedControlsFound.setEnabled(true);
						}
						
						List<String> disabledControls = SCIP.getControlsName(response, "disabled=");						
						for (String disabledControl : disabledControls) {
							if (!listModelOtherControls.contains(disabledControl)) {
								controlEvents.put(disabledControl, SCIP.getControlEvents(response,disabledControl));
								listModelOtherControls.addElement(disabledControl);
							}
							disabledControlsFound.setEnabled(true);
						} 
								
						
						//parse all controls = input,radio,checkbox
						List<String> controlNames = SCIP.getControlsName(response,"");
						listModel.clear();
						for (String controlName : controlNames) {
							if (!listModelOtherControls.contains(controlName)) {
								controlEvents.put(controlName, SCIP.getControlEvents(response,controlName));
								listModel.addElement(controlName);
							}
						}
						
						try {
							//generate a page object
							dotNetPage = new DotNetPage(URL,response,controlEvents);
							
							//set captions
							jCheckBoxEventValidationFound.setText("Event Validation.");
							
							//fill the checkboxes with the right values
							jCheckBoxViewStateFound.setSelected(dotNetPage.getViewState().getText()!="");
							jCheckBoxViewStateSigned.setSelected(dotNetPage.getViewState().isViewStateMACSigned());
							jCheckBoxViewStateEncrypted.setSelected(dotNetPage.getViewState().isEncrypted());
							jCheckBoxEventValidationFound.setSelected(dotNetPage.getViewState().isEventValidationExists());
							invisibleControlsFound.setEnabled(dotNetPage.getViewState().isInvisibleControls());
							/*
							if (!jCheckBoxEventValidationFound.isSelected()) {
								jCheckBoxEventValidationFound.setText("Event Validation. (enumeration not avalidable)");
							}*/
							
							//enable enumeration only if event validation exists
							jButtonEnumControls.setEnabled(jCheckBoxEventValidationFound.isSelected());
							
							//always enabled controls
							jButtonAddControl.setEnabled(jCheckBoxViewStateFound.isSelected());
							jButtonBlindEnumControls.setEnabled(jCheckBoxViewStateFound.isSelected());
							jButtonParseControlsFromHistory.setEnabled(jCheckBoxViewStateFound.isSelected());
							jButtonParseControlsFromURL.setEnabled(jCheckBoxViewStateFound.isSelected());
							
							
							
							jCheckBoxEventValidationSigned.setSelected(dotNetPage.getViewState().isEventValidationSigned());
							/*
							listModel.clear();
							for (String controlName : dotNetPage.getControlEvents().keySet()) {
								listModel.addElement(controlName);
							}
							*/
							//print viewstate
							//System.out.println(dotNetPage.getViewState().getViewStateMap());
							//print eventvalidation
							//System.out.println(dotNetPage.getViewState().getEventValidationMap());
							//dotNetPage.getViewState().AddEvent("ctl00$MainContent$Button2","");
							//System.out.println(dotNetPage.getViewState().getEventValidationMap());
						}
						catch (Exception exception) {
							System.out.println(exception.toString());
						}
						listOtherControls.setModel(listModelOtherControls);
						listVisibleControls.setModel(listModel);
						
						//clear results and events
						resultsModel = new DefaultTableModel();
						resultsModel.setDataVector(new Vector<Vector<String>>(), vectorResultsColumns);
						tableResults.setModel(resultsModel);
						listEvents = new JList<>();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "URL should begin with \"http://\" or \"https://\"","Error",JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "Enumerate Controls":
				if (dotNetPage!=null) {
					final EnumerateControls enumerateControls = new EnumerateControls(orgMessage,getRequest(""),stringControlName.substring(0, stringControlName.lastIndexOf("$")+1),proxy);
					enumerateControls.addWindowListener(new java.awt.event.WindowAdapter() {  
		                public void windowClosing(java.awt.event.WindowEvent e) {  
		                	if (enumerateControls.getFoundControls().size()>0) {
								for (String controlName : enumerateControls.getFoundControls()) {
									addControlToList(controlName,enumerateControls.getURL(),true,"Results");
								}
							}  
		                      
		                }  
		            });  
					
				}
				break;
			case "About":
				showAboutDialog();
				break;
			case "Exit":
				frame.dispose();
				break;
			case "Controls From URL":
				showAddControlsFromUrlDialog();
				break;
			case "Add":
				String prefix = "";
				if (stringControlName!=null) {
					prefix = stringControlName.substring(0, stringControlName.lastIndexOf("$")+1);
				}
				String controlName = JOptionPane.showInputDialog(frame,"Add Control",prefix);
				addControlToList(controlName,stringURL,false,"PageControls");
				
				break;
			case "Controls From History":
				showControlsFromHistory();
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addControlToList(String controlName,String URL,boolean hidden,String List) {
		if (controlName!=null) {
			boolean ControlExists = false;
			for (Enumeration<String> control = listModel.elements();control.hasMoreElements();) {
				if (control.nextElement().toLowerCase().equals(controlName.toLowerCase())) {
					ControlExists = true;
				}
			}
			/*
			if (resultsModel.getRowCount()>0) {
				for (int i=1; i<=resultsModel.getRowCount();i++) {
					resultsModel.getDataVector().elements()
					if (control.nextElement().toLowerCase().equals(controlName.toLowerCase())) {
						ControlExists = true;
					}
				}
			}*/
			for (Enumeration<Vector<String>> control = resultsModel.getDataVector().elements();control.hasMoreElements();) {
				if (control.nextElement().get(0).toString().toLowerCase().equals(controlName.toLowerCase())) {
					ControlExists = true;
				}
			}
			if (!ControlExists) {
				if (List=="PageControls") {
					listModel.addElement(controlName);
				}
				else {
					resultsModel.addRow(new Vector<String>(Arrays.asList(controlName,URL,hidden==true?"Yes":"No")));
				}
			}
			
			dotNetPage.getViewState().AddEvent(controlName,"");
			//update controls list
			listVisibleControls.setModel(listModel);
			tableResults.setModel(resultsModel);	
		}
	}
	
	private void addResponseControls(HttpMessage message) {
		//hashmap to hold the control-events list (ie: control ctl00 has the following events - onclick,onchage etc)
		HashMap<String,List<String>> controlEvents = new HashMap<String,List<String>>();
		String response = message.getResponseBody().toString();
		if (response!="") {
			//parse all controls = input,radio,checkbox
			List<String> names = SCIP.getControlsName(response,"");
			Iterator<String> itr = names.iterator();
			while (itr.hasNext()) {
				String curName = itr.next();
				controlEvents.put(curName, SCIP.getControlEvents(response,curName));
			}
			
			//update page with controls
			dotNetPage.addControlEvents(controlEvents);
			
			for (String controlName : controlEvents.keySet()) {
				try {
					if (message.getRequestHeader().getURI().getURI() == stringURL) {
						addControlToList(controlName,message.getRequestHeader().getURI().toString(),false,"PageControls");
					}
					else
					{
						addControlToList(controlName,message.getRequestHeader().getURI().toString(),false,"Results");
					}
				} catch (URIException e) {
					e.printStackTrace();
				}
				
			}
			
			
		}
	}
	
	private void showAboutDialog(){
		ImageIcon icon = new ImageIcon(SCIP.class.getResource("resource/icon/Hacktics_logo_verblue.jpg")); 
		JOptionPane.showMessageDialog(frame,
				ABOUT,
				"About "+ APP_NAME,
				JOptionPane.INFORMATION_MESSAGE,icon);
	}
	
	private void showAddControlsFromUrlDialog() {
		String dialogInputURL = JOptionPane.showInputDialog(frame,"URL");
		
		//get page response
		if ((dialogInputURL!="null") && (dialogInputURL!=null)) {
			HttpMessage urlMessage;
			urlMessage = orgMessage.cloneRequest();
			try {
				urlMessage.getRequestHeader().setURI(new URI(dialogInputURL,true));
			} catch (URIException | NullPointerException e) {
				e.printStackTrace();
			}
			ZAP.send(urlMessage);
			addResponseControls(urlMessage);
			
		}
	}
	
	private void showControlsFromHistory() {
		ArrayList<String> URLs = ZAP.getDomainsFromZap();
		
		String dialogInputURL = (String) JOptionPane.showInputDialog(frame,
				"Choose Domain", "Input",
				JOptionPane.INFORMATION_MESSAGE, null,
				URLs.toArray(), null);
		
		if (dialogInputURL!=null) {
			ArrayList<HttpMessage> Responses = ZAP.getZAPResponsesForURL(dialogInputURL);
			if (Responses.size()>0) {
				for (HttpMessage response : Responses) {
					addResponseControls(response);
				}
			}
		}
		
	}
	//http://stackoverflow.com/questions/4924294/is-there-a-way-to-customise-javas-setenabledfalse
	public class ReadOnlyCheckBox extends JCheckBox {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 5718046115962241035L;

		public ReadOnlyCheckBox (String text) {
	        super(text);
	    }
		
		public void setSelected (boolean selected) {
			super.setSelected(selected);
			super.setEnabled(selected);
		}

	    protected void processKeyEvent(KeyEvent e) {
	    }

	    protected void processMouseEvent(MouseEvent e) {

	    }
	}

}
