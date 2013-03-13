package org.zaproxy.zap.extension.scip;

import java.util.List;
import java.util.ResourceBundle;

import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;

import com.hacktics.scip.SCIP;
import com.hacktics.viewstate.ViewState;


public class SCIPPassiveScanner extends PluginPassiveScanner {

	private PassiveScanThread parent = null;
	private Logger logger = Logger.getLogger(this.getClass());
	private ViewState viewstate;
    private ResourceBundle messages = null;
    public static final String NAME = "SCIP";


	@Override
	public void setParent (PassiveScanThread parent) {
		this.parent = parent;
	}

	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {
		// Ignore
	}

	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
		String response = msg.getResponseBody().toString();
		
		if (response.length()>0) {
    		try {
				//generate a page object
    			viewstate = new ViewState(response);
				if(viewstate.isInvisibleControls()) {
					if (viewstate.isEventValidationExists() && viewstate.isEventValidationSigned()) {
						raiseAlertInvisible(msg,id,"Invisible Controls (Protected)",Alert.RISK_MEDIUM);
					}
					else {
						raiseAlertInvisible(msg,id,"Invisible Controls (Exploitable)",Alert.RISK_HIGH);
					}
				}
				
				//add commented and disabled buttons
				List<String> commentedControls = SCIP.getComments(response);				
				for (String commentedControl : commentedControls) {
					raiseAlertCommented(msg,id,commentedControl);
				}
				
				List<String> disabledControls = SCIP.getControlsName(response, "disabled=");						
				for (String disabledControl : disabledControls) {
					raiseAlertDisabled(msg,id,disabledControl);
				} 
			
    		}
    		catch (Exception ex) {
    			logger.error(ex.getMessage(), ex);		
			}
    	}

	}
	
	private void raiseAlertInvisible(HttpMessage msg, int id, String text, int risk) {
	    Alert alert = new Alert(getId(), risk, Alert.SUSPICIOUS, 
		    	text);
		    	alert.setDetail(
		    		"Traces of Invisible Controls Found in ViewState", 
		    		msg.getRequestHeader().getURI().toString(),
		    		"__VIEWSTATE", "", "",
		    		"scip solution", 
		            "scip reference", 
		            msg);
	
    	parent.raiseAlert(id, alert);

	}
	
	private void raiseAlertDisabled(HttpMessage msg, int id,String control) {
	    Alert alert = new Alert(getId(), Alert.RISK_MEDIUM, Alert.SUSPICIOUS, 
		    	"Disabled Controls");
		    	alert.setDetail(
		    		"Disabled Controls Found in response.", 
		    		msg.getRequestHeader().getURI().toString(),
		    		control, "", "",
		    		"scip solution", 
		            "scip reference", 
		            msg);
	
    	parent.raiseAlert(id, alert);

	}
	
	private void raiseAlertCommented(HttpMessage msg, int id,String control) {
	    Alert alert = new Alert(getId(), Alert.RISK_MEDIUM, Alert.SUSPICIOUS, 
		    	"Commented Controls");
		    	alert.setDetail(
		    		"Commented Controls Found in response.", 
		    		msg.getRequestHeader().getURI().toString(),
		    		control, "", "",
		    		"scip solution", 
		            "scip reference", 
		            msg);
	
    	parent.raiseAlert(id, alert);

	}

	private int getId() {
		return 10092;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	public String getMessageString (String key) {
		return messages.getString(key);
	}

}
