package com.hacktics.vehicle.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMessage;

import com.hacktics.vehicle.Dictionary;
import com.hacktics.vehicle.ZAP;

public class EnumerateControls extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2145387227181332965L;
	private JTextArea jTextAreaControlConsole;
	private JTextField jTextPrefix;
	
	private JLabel jLabelURL = new JLabel("URL:");
	private JLabel jLabelControlsFound = new JLabel("Controls Found: 0");
	private static int numFoundControls;
	private JTextField jTextURL;
	private ArrayList<String> foundControlsArray = new ArrayList<String>();
	public ArrayList<String> getFoundControls() {
		return foundControlsArray;
	}
	
	private String baseRequest = "";
	private String baseControlName = "";
	private HttpMessage enumMessage;
	private String URL;
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
	
	public EnumerateControls (HttpMessage msg,String BaseRequest,String BaseControlName,Proxy proxy) {
		
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
		
		jTextAreaControlConsole = new JTextArea(10,30);
		jTextAreaControlConsole.setLineWrap(true);
		//jTextAreaControlConsole.setPreferredSize(new Dimension(300,300));
		JScrollPane jScrollPaneTextArea = new JScrollPane(jTextAreaControlConsole);
		jScrollPaneTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.gridy = 3;
		gbc.insets = new Insets(0,5,5,5);
		contentPane.add(jScrollPaneTextArea,gbc);
		
		
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
		setTitle("Control Enumeration");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(300,300);
		
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
		
		
		//perform operation in new thread to update textarea control in run
		//http://stackoverflow.com/questions/2710712/output-to-jtextarea-in-realtime
		(new Thread()
		{	
			public void run() {
				for (final String strPrefix : prefixControlArray) {
					for (final String strControlName : controlNamesArray)
					{     
				    	jTextAreaControlConsole.append("Trying:" + strPrefix + strControlName + "\r\n");
				    	String requestBody = baseRequest+"&"+stringControlNameConvention+strPrefix+strControlName+"=Button";
				    	enumMessage.setRequestBody(requestBody.getBytes());
				    	enumMessage.getRequestHeader().setContentLength(requestBody.length());
				    	
				    	
				    	ZAP.send(enumMessage);
				    	String stringResponse = enumMessage.getResponseHeader().getPrimeHeader();
				    	
				    	//String stringResponse = URLReader.postURLResponse(URL, baseRequest+"&"+stringControlNameConvention+strPrefix+strControlName+"=Button",proxy);
				    	
				    	if (stringResponse.startsWith("HTTP/1.1 500 Internal Server Error")) {
				    		String newControl = stringControlNameConvention + strPrefix + strControlName;
							jTextAreaControlConsole.append("Found Control:" + newControl + "\r\n");
							

							//check duplicates
							boolean foundDuplicate = false;
							for (String controlName : foundControlsArray) {
								if (controlName.compareToIgnoreCase(newControl)==0) {
									foundDuplicate = true;
								}
							}
							if (!foundDuplicate) {
								//inscrease counter for number of found controls
								numFoundControls++;
								jLabelControlsFound.setText("Controls Found: "+numFoundControls);
							}
							foundControlsArray.add(newControl);		
								
								
							
						}
				    	jTextAreaControlConsole.setCaretPosition(jTextAreaControlConsole.getText().length());
				    }
		
						
				}
			}
		}).start();
		
		
		
	}


	
	
}
