package com.hacktics.vehicle.dejavu.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JList;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.zaproxy.zap.network.HttpRequestBody;

import com.hacktics.vehicle.DotNetPage;
import com.hacktics.vehicle.VEHICLE;
import com.hacktics.vehicle.ZAP;
import com.hacktics.vehicle.dejavu.wayback;
import com.hacktics.viewstate.ViewState;
import com.hacktics.viewstate.editor.ViewStateEditor;

public class DejaVuView extends JPanel implements ActionListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5943836967867945169L;

	private JTextField textURL;
	
	private DotNetPage dotNetPage;
	private DotNetPage dotNetPageDejavu; // Alon
	private String year = "2011"; 
	private JButton btnEditor;
	private JButton btnEditDejavuViewstate;
	private JButton btnRunEvent;
	private JButton btnPostBack;
	
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> list_DejavuControls;
	private HttpMessage orgMessage;
	String URL = "";
	String response = "";
	String stringControlName = "";
	
	//page analysis
	JLabel lblChangeInViewstate = new JLabel("Viewstate");
	JLabel lblChangeInEventValidation = new JLabel("Event Validation");
	
	Icon yellowFlag = new ImageIcon(getClass().getResource("/resource/icon/16/074.png"));
	Icon greenFlag = new ImageIcon(getClass().getResource("/resource/icon/16/072.png"));

	
	/**
	 * Create the panel.
	 */
	public DejaVuView(HttpMessage hm) {
		
		//keep message for use in resend
		orgMessage = hm;
		if (hm!=null) {
			try {
				URL = hm.getRequestHeader().getURI().getURI();
			} catch (URIException e) {
				e.printStackTrace();
			}
		}
		
		initComponents();
		
	}
	
	public void initComponents() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{5, 100, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{5, 35, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_URL = new JPanel();
		GridBagConstraints gbc_panel_URL = new GridBagConstraints();
		gbc_panel_URL.anchor = GridBagConstraints.NORTH;
		gbc_panel_URL.gridwidth = 4;
		gbc_panel_URL.insets = new Insets(1, 0, 5, 5);
		gbc_panel_URL.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_URL.gridx = 1;
		gbc_panel_URL.gridy = 1;
		add(panel_URL, gbc_panel_URL);
		GridBagLayout gbl_panel_URL = new GridBagLayout();
		gbl_panel_URL.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_URL.rowHeights = new int[]{0, 0};
		gbl_panel_URL.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_URL.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_URL.setLayout(gbl_panel_URL);
		
		JLabel lblURL = new JLabel("URL:");
		GridBagConstraints gbc_lblURL = new GridBagConstraints();
		gbc_lblURL.insets = new Insets(0, 1, 0, 5);
		gbc_lblURL.anchor = GridBagConstraints.EAST;
		gbc_lblURL.gridx = 0;
		gbc_lblURL.gridy = 0;
		panel_URL.add(lblURL, gbc_lblURL);
		
		textURL = new JTextField(URL);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 1, 0, 6);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel_URL.add(textURL, gbc_textField);
		textURL.setColumns(10);
		
		JButton btnGetURL = new JButton("Deja Vu");
		btnGetURL.setActionCommand("btnGet");
		btnGetURL.addActionListener(this);
		
		GridBagConstraints gbc_btnGetURL = new GridBagConstraints();
		gbc_btnGetURL.gridx = 2;
		gbc_btnGetURL.gridy = 0;
		panel_URL.add(btnGetURL, gbc_btnGetURL);
		

		
		JPanel panelDate = new JPanel();
		panelDate.setBorder(BorderFactory.createTitledBorder("Date"));
		GridBagConstraints gbc_panelDate = new GridBagConstraints();
		gbc_panelDate.anchor = GridBagConstraints.NORTH;
		gbc_panelDate.insets = new Insets(0, 0, 5, 5);
		gbc_panelDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelDate.gridx = 1;
		gbc_panelDate.gridy = 2;
		add(panelDate, gbc_panelDate);
		GridBagLayout gbl_panelDate = new GridBagLayout();
		gbl_panelDate.columnWidths = new int[]{0, 0};
		gbl_panelDate.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelDate.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelDate.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		panelDate.setLayout(gbl_panelDate);
		
		JLabel lblYear = new JLabel("Select Year:");
		GridBagConstraints gbc_lblYear = new GridBagConstraints();
		gbc_lblYear.anchor = GridBagConstraints.WEST;
		gbc_lblYear.insets = new Insets(0, 0, 5, 0);
		gbc_lblYear.gridx = 0;
		gbc_lblYear.gridy = 0;
		panelDate.add(lblYear, gbc_lblYear);
		
		String[] WByears={"2005","2006","2007","2008","2009","2010","2011","2012","2013"};
		
		JComboBox<Object> comboBox = new JComboBox<Object>(WByears);
		comboBox.setSelectedIndex(6);
		comboBox.setActionCommand("SetYear");
		comboBox.addActionListener(this);
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 1;
		panelDate.add(comboBox, gbc_comboBox);
		lblChangeInViewstate.setEnabled(false);
		
		//initialize page analysis labels
		lblChangeInViewstate.setIcon(greenFlag);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Deja Vu Analysis"));
		GridBagConstraints gbc_infoPanel = new GridBagConstraints();
		gbc_infoPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_infoPanel.anchor = GridBagConstraints.NORTH;
		gbc_infoPanel.gridwidth = 2;
		gbc_infoPanel.insets = new Insets(0, 0, 5, 5);
		gbc_infoPanel.gridx = 3;
		gbc_infoPanel.gridy = 2;
		add(infoPanel, gbc_infoPanel);
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_infoPanel.rowHeights = new int[]{0, 0, 0};
		gbl_infoPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_infoPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);
		
		GridBagConstraints gbc_lblChangeInViewstate = new GridBagConstraints();
		gbc_lblChangeInViewstate.anchor = GridBagConstraints.WEST;
		gbc_lblChangeInViewstate.insets = new Insets(0, 0, 5, 5);
		gbc_lblChangeInViewstate.gridx = 0;
		gbc_lblChangeInViewstate.gridy = 0;
		infoPanel.add(lblChangeInViewstate, gbc_lblChangeInViewstate);
		
		btnEditor = new JButton("Edit Viewstate");
		btnEditor.setEnabled(false);
		btnEditor.setActionCommand("btn_Editor");
		btnEditor.addActionListener(this);
		
		GridBagConstraints gbc_btnEditor = new GridBagConstraints();
		gbc_btnEditor.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditor.gridx = 3;
		gbc_btnEditor.gridy = 0;
		infoPanel.add(btnEditor, gbc_btnEditor);
		
		btnEditDejavuViewstate = new JButton("Edit Deja Vu Viewstate");
		btnEditDejavuViewstate.setEnabled(false);
		btnEditDejavuViewstate.setActionCommand("btn_EditDejavuViewstate");
		btnEditDejavuViewstate.addActionListener(this);
		lblChangeInEventValidation.setEnabled(false);
		
		lblChangeInEventValidation.setIcon(greenFlag);		
		
		GridBagConstraints gbc_lblChangeInEventValidation = new GridBagConstraints();
		gbc_lblChangeInEventValidation.anchor = GridBagConstraints.WEST;
		gbc_lblChangeInEventValidation.insets = new Insets(0, 0, 0, 5);
		gbc_lblChangeInEventValidation.gridx = 0;
		gbc_lblChangeInEventValidation.gridy = 1;
		infoPanel.add(lblChangeInEventValidation, gbc_lblChangeInEventValidation);
		GridBagConstraints gbc_btnEditDejavuViewstate = new GridBagConstraints();
		gbc_btnEditDejavuViewstate.gridx = 3;
		gbc_btnEditDejavuViewstate.gridy = 1;
		infoPanel.add(btnEditDejavuViewstate, gbc_btnEditDejavuViewstate);
		
		JPanel panel_Controls = new JPanel();
		panel_Controls.setBorder(BorderFactory.createTitledBorder("Deja Vu Controls"));
		GridBagConstraints gbc_panel_Controls = new GridBagConstraints();
		gbc_panel_Controls.insets = new Insets(0, 0, 5, 5);
		gbc_panel_Controls.gridwidth = 2;
		gbc_panel_Controls.gridheight = 3;
		gbc_panel_Controls.fill = GridBagConstraints.BOTH;
		gbc_panel_Controls.gridx = 3;
		gbc_panel_Controls.gridy = 3;
		add(panel_Controls, gbc_panel_Controls);
		GridBagLayout gbl_panel_Controls = new GridBagLayout();
		gbl_panel_Controls.columnWidths = new int[]{0, 10, 0, 0};
		gbl_panel_Controls.rowHeights = new int[]{0, 150, 0, 0, 0, 0, 0};
		gbl_panel_Controls.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_Controls.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		panel_Controls.setLayout(gbl_panel_Controls);
		
		list_DejavuControls = new JList<String>(listModel);
		list_DejavuControls.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if (((JList<?>) evt.getSource()).getSelectedIndex()>-1) {
					stringControlName = ((JList<?>) evt.getSource()).getSelectedValue().toString();
					if (stringControlName.equals("")) return;
					btnRunEvent.setEnabled(true);
					
				}
				
			}
		});
		JScrollPane list_DejavuControlsScrollable = new JScrollPane(list_DejavuControls);

		GridBagConstraints gbc_list_DejavuControls = new GridBagConstraints();
		gbc_list_DejavuControls.ipady = 10;
		gbc_list_DejavuControls.ipadx = 10;
		gbc_list_DejavuControls.gridwidth = 3;
		gbc_list_DejavuControls.gridheight = 3;
		gbc_list_DejavuControls.insets = new Insets(0, 0, 5, 0);
		gbc_list_DejavuControls.fill = GridBagConstraints.BOTH;
		gbc_list_DejavuControls.gridx = 0;
		gbc_list_DejavuControls.gridy = 1;
		panel_Controls.add(list_DejavuControlsScrollable, gbc_list_DejavuControls);
		
		btnRunEvent = new JButton("Run Event");
		btnRunEvent.setEnabled(false);
		btnRunEvent.setActionCommand("Run Event");
		btnRunEvent.addActionListener(this);
		
		GridBagConstraints gbc_btnRunEvent = new GridBagConstraints();
		gbc_btnRunEvent.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRunEvent.insets = new Insets(0, 0, 5, 5);
		gbc_btnRunEvent.gridx = 0;
		gbc_btnRunEvent.gridy = 4;
		panel_Controls.add(btnRunEvent, gbc_btnRunEvent);
		
		btnPostBack = new JButton("Post Back");
		btnPostBack.setEnabled(false);
		btnPostBack.setActionCommand("Post Back");
		btnPostBack.addActionListener(this);
		
		GridBagConstraints gbc_btnPostBack = new GridBagConstraints();
		gbc_btnPostBack.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPostBack.insets = new Insets(0, 0, 5, 0);
		gbc_btnPostBack.gridx = 2;
		gbc_btnPostBack.gridy = 4;
		panel_Controls.add(btnPostBack, gbc_btnPostBack);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "SetYear":
			JComboBox<?> cb = (JComboBox<?>)e.getSource();
			 year = (String)cb.getSelectedItem();
			//JOptionPane.showMessageDialog(frame,"Selected Year:  " + WByear);
			    btnEditDejavuViewstate.setEnabled(false);
				btnEditor.setEnabled(false);
				ProcessDejuvu();
				listModel = new DefaultListModel<String>();
			break;
		case "btnGet":
		
			ProcessDejuvu();
			
		break;
		case "btn_EditDejavuViewstate":
			if (dotNetPageDejavu.getViewState().getViewStateText()!="") {
				final ViewStateEditor viewDejavuStateEditor = new ViewStateEditor(dotNetPageDejavu.getViewState(),dotNetPageDejavu.getViewState().isViewStateMACSigned());
			}
			
			break;
		case "btn_Editor":
			if (dotNetPage.getViewState().getViewStateText()!="") {
				final ViewStateEditor viewStateEditor = new ViewStateEditor(dotNetPage.getViewState(),dotNetPage.getViewState().isViewStateMACSigned());
			}
			
			break;
		case "Run Event":
			if (dotNetPage!=null) {
				HttpMessage eventMessage;
				String stringRequest = VEHICLE.getRequest(stringControlName,dotNetPageDejavu.getViewState());
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
		case "Post Back":
			if (dotNetPage!=null) {
				HttpMessage eventMessage;
				String stringRequest = VEHICLE.getRequest("",dotNetPageDejavu.getViewState());
				//System.out.println(stringRequest);
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
		}
		
	}
	
	public void ProcessDejuvu()
	{
		//btnEditDejavuViewstate.setEnabled(true);
		//btnEditor.setEnabled(true);
		String URL = textURL.getText();

		// ++
		wayback vehicleDejaVu = new wayback();
		String responseDejavu = "";
		// ++
		
		//reset controls
		lblChangeInViewstate.setEnabled(false);
		lblChangeInEventValidation.setEnabled(false);
		btnRunEvent.setEnabled(false);
		btnPostBack.setEnabled(false);
		btnEditDejavuViewstate.setEnabled(false);
		btnEditor.setEnabled(false);
		listModel = new DefaultListModel<String>();
		list_DejavuControls.setModel(listModel);
		
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
		responseDejavu = vehicleDejaVu.GetResponseString(URL,year);
		if (response.length()>0 && responseDejavu.length()>0 ) {
			// generate a page object

			lblChangeInViewstate.setEnabled(true);
			lblChangeInEventValidation.setEnabled(true);
			btnPostBack.setEnabled(true);


			// WBResponseString=
			// ScipWb.GetResponseString(Utils.get_Site(),
			// String.valueOf(Utils.get_eYear()) ); //from Niv

			dotNetPage = new DotNetPage(URL, response,	new HashMap<String,List<String>>()); // current
			ViewState CurrentVS = dotNetPage.getViewState();
			String strCurrentVS = CurrentVS.getViewStateText();
			List<String> CurrentEV = CurrentVS.getEventValidationArray();
		
			dotNetPageDejavu = new DotNetPage(URL,responseDejavu,new HashMap<String,List<String>>());
			ViewState viewstateDejavu = dotNetPageDejavu.getViewState();
			String strVSDejavu = viewstateDejavu.getViewStateText();
			List<String> arrayEVDejavu = viewstateDejavu.getEventValidationArray();

			//enable viewstate editors
			btnEditor.setEnabled(strCurrentVS!="");
			btnEditDejavuViewstate.setEnabled(strVSDejavu!="");
			
			Boolean FoundItem=false;
			String CurrX=null;
			String CurrY=null;

			List<String> NonExistItems = new ArrayList<String>();
			List<String> CurrCntrl = new ArrayList<String>();
			List<String> WbCntrl = new ArrayList<String>();
			List<String> NonExistCntrl = new ArrayList<String>();


			//check for viewstate and event validation differences
			if (strCurrentVS.equals("")) 
			{
				btnEditor.setEnabled(false);
			}

			if (strVSDejavu.equals(""))
			{

				btnEditDejavuViewstate.setEnabled(false);
			}
			
			if (strVSDejavu.equals(strCurrentVS)) {
				lblChangeInViewstate.setText("Same Viewstate");
				lblChangeInViewstate.setIcon(greenFlag);
			}
			else {
				lblChangeInViewstate.setText("Difference In Viewstate");
				lblChangeInViewstate.setIcon(yellowFlag);
			}
			
			if (arrayEVDejavu.equals(CurrentEV))  {
				lblChangeInEventValidation.setText("Same Event Validation");
				lblChangeInEventValidation.setIcon(greenFlag);
			}
			else {
				lblChangeInEventValidation.setText("Difference In Event Validation");
				lblChangeInEventValidation.setIcon(yellowFlag);
			}



			for (int x=0;x<arrayEVDejavu.size();x++)
			{
				CurrX=arrayEVDejavu.get(x);
				FoundItem=false;
				for (int y=0;y<CurrentEV.size();y++)
				{

					CurrY=CurrentEV.get(y);
					if (CurrX.equals(CurrY))
					{
						FoundItem = true;
						break;
					}
				}
				if (FoundItem==false ) 	NonExistItems.add(CurrX);						
			}


			//		if (NonExistItems.size()>0)
			//		{
			// Control Names
			WbCntrl = VEHICLE.getControlsName(responseDejavu, "");
			CurrCntrl = VEHICLE.getControlsName(response, "");
			


			for (int x=0;x<WbCntrl.size();x++)
			{
				CurrX=WbCntrl.get(x);
				FoundItem=false;
				for (int y=0;y<CurrCntrl.size();y++)
				{

					CurrY=CurrCntrl.get(y);
					if (CurrX.equals(CurrY))
					{
						FoundItem = true;
						break;
					}
				}
				if (FoundItem==false ) 
				{
					NonExistCntrl.add(CurrX);
					listModel.addElement(CurrX);

				}
			}
			list_DejavuControls.setModel(listModel);
		}
	}
}
