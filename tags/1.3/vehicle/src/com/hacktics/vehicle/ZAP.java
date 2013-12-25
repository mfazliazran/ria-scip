package com.hacktics.vehicle;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.JOptionPane;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.db.TableHistory;
import org.parosproxy.paros.extension.history.ExtensionHistory;
import org.parosproxy.paros.extension.manualrequest.ManualRequestEditorDialog;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.Session;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;


public class ZAP {
	
	private static long zapSessionID;
	
	public static final boolean ALLOW_STATE = true;
	public static final int INITIATOR = HttpSender.MANUAL_REQUEST_INITIATOR;
	private static HttpSender sender = new HttpSender(Model.getSingleton().getOptionsParam().getConnectionParam(), ALLOW_STATE,INITIATOR);
	//private static HttpSender sender = new HttpSender(Model.getSingleton().getOptionsParam().getConnectionParam(), ALLOW_STATE);
	
	public ZAP() {
		
	}

	/**
	 * 
	 * @return Returns all urls in the current zap session
	 */
	public static ArrayList<String> getDomainsFromZap() {
		ArrayList<String> result = new ArrayList<String>();
		Session session = Model.getSingleton().getSession();
		SiteNode root = (SiteNode) session.getSiteTree().getRoot();
		@SuppressWarnings("unchecked")
		Enumeration<SiteNode> en = root.children();
		while (en.hasMoreElements()) {
			String site = en.nextElement().getNodeName();
			
			if (site.indexOf("//") >= 0) {
				site = site.substring(site.indexOf("//") + 2);
			}
			/*
			if (site.indexOf(":") >= 0) {
				site = site.substring(0, site.indexOf(":"));
			}*/
			result.add(site);
			
		}

		return result;
	}
	
	public static int getProxyPort() {
		return Model.getSingleton().getOptionsParam().getProxyParam().getProxyPort();
	}
	
	public static String getProxyIP() {
		return Model.getSingleton().getOptionsParam().getProxyParam().getProxyIp();
	}
	
	public static ArrayList<HttpMessage> getZAPResponsesForURL(String URL) {
		ArrayList<HttpMessage> result = new ArrayList<HttpMessage>();
		
		zapSessionID = Model.getSingleton().getSession().getSessionId();// Read only current session's history
		
		try {
			Vector<Integer> historyIDList;
			TableHistory tableHistory = Model.getSingleton().getDb()
					.getTableHistory();
			historyIDList = tableHistory.getHistoryList(zapSessionID);

			for (Integer historyID : historyIDList) {
				String historyURL = tableHistory.read(historyID).getHttpMessage().getRequestHeader().getURI().getURI();
				historyURL = historyURL.substring(historyURL.indexOf("//") + 2);
				if (historyURL.startsWith(URL)) {
					result.add(tableHistory.read(historyID).getHttpMessage());
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void showZapRepeater(HttpMessage hm) {
		ExtensionHistory extHist = (ExtensionHistory) Control.getSingleton().getExtensionLoader().getExtension("ExtensionHistory");
		if (extHist != null) {
			ManualRequestEditorDialog dialog = extHist.getResendDialog();
			dialog.setMessage(hm);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
		}
	}
	
	public static void send(HttpMessage msg) {
		try {
			sender.sendAndReceive(msg);
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Unknown host:" + msg.getRequestHeader().getHostName(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Timeout for host:" + msg.getRequestHeader().getHostName(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"An error has occoured - " + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
