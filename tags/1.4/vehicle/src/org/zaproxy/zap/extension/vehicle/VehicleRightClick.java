package org.zaproxy.zap.extension.vehicle;

import org.apache.log4j.Logger;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.view.PopupMenuHttpMessage;

import com.hacktics.vehicle.VEHICLE;

@SuppressWarnings("unused")
public class VehicleRightClick extends PopupMenuHttpMessage {

	private static final long serialVersionUID = -8497133967174196949L;
	private VehicleExtension extension = null;
	private final Logger logger = Logger.getLogger(this.getClass());

	/**
     * @param label
     */
	public VehicleRightClick(String label) {
		super(label);
	}
	
	/**
	 * 
	 * @param extension
	 */
	public void setExtension(VehicleExtension extension) {
		this.extension = extension;
	}
	
	@Override
	public void performAction(HttpMessage msg) throws Exception {
		new VEHICLE(msg);
		logger.info("VEHICLE Loaded via Right-Click");
	}

	@Override
	public boolean isEnableForInvoker(Invoker invoker) {
		return true;
	}
	
	
	

}
