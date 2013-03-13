package org.zaproxy.zap.extension.scip;

import org.apache.log4j.Logger;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.view.PopupMenuHttpMessage;

import com.hacktics.scip.SCIP;

@SuppressWarnings("unused")
public class RightClickSCIP extends PopupMenuHttpMessage {

	private static final long serialVersionUID = -8497133967174196949L;
	private SCIPExtension extension = null;
	private final Logger logger = Logger.getLogger(this.getClass());

	/**
     * @param label
     */
	public RightClickSCIP(String label) {
		super(label);
	}
	
	/**
	 * 
	 * @param extension
	 */
	public void setExtension(SCIPExtension extension) {
		this.extension = extension;
	}
	
	@Override
	public void performAction(HttpMessage msg) throws Exception {
		new SCIP(msg);
		logger.info("SCIP Loaded via Right-Click");
	}

	@Override
	public boolean isEnableForInvoker(Invoker invoker) {
		return true;
	}
	
	
	

}
