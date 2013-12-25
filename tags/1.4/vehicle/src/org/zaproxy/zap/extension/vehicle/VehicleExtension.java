
/*

 */

package org.zaproxy.zap.extension.vehicle;

import com.hacktics.vehicle.VEHICLE;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;

/*
 * This class defines the extension.
 */
public class VehicleExtension extends ExtensionAdaptor {

    private JMenuItem mnuControlEvents = null;
    private VehicleRightClick popupMsgControlEvents = null;
    private ResourceBundle messages = null;
    
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public static final String NAME = "VEHICLE"; 
  
	    public VehicleExtension() {
	        super();
	        initialize();
	
	    }
	
	    /**
	     * @param name
	     */
	    public VehicleExtension(String name) {
	        super(name);
	    }

        /**
         * This method initializes this
         * 
         * @return void
         */
        private void initialize() {
            this.setName(NAME); 	
        	messages = ResourceBundle.getBundle(
            		this.getClass().getPackage().getName() + ".Messages", Constant.getLocale());	

        }
        
        @Override
        public void hook(ExtensionHook extensionHook) {
            super.hook(extensionHook);
            
            if (getView() != null) {
                // Register our top menu item, as long as we're not running as a daemon
                extensionHook.getHookMenu().addToolsMenuItem(getmnuVEHICLE());
        	    	// Register our popup menu item, as long as we're not running as a daemon
        	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupControlEvents());
        	    }
        }


        private JMenuItem getmnuVEHICLE() {
        if (mnuControlEvents == null) {
                mnuControlEvents = new JMenuItem();
                mnuControlEvents.setText(messages.getString("vehicle.menu"));

                mnuControlEvents.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                        // This is where you do what you want to do.
                        // In this case we'll just show a popup message.
                	try {
	                		new VEHICLE(null);
	                		logger.info("VEHICLE Loaded");
	                	}
	                	catch (Exception ex) {
	                		logger.error(ex.getMessage(), ex);
	                	}
                	}
                });
        	}
        	return mnuControlEvents;
        }
        
        private VehicleRightClick getPopupControlEvents() {
    		if (popupMsgControlEvents  == null) {
    			popupMsgControlEvents = new VehicleRightClick(messages.getString("vehicle.menu.rightclick"));
    			popupMsgControlEvents.setExtension(this);
    		}
    		return popupMsgControlEvents;
    	}

        @Override
        public String getAuthor() {
                return messages.getString("vehicle.author");
        }

        @Override
        public String getDescription() {
                return messages.getString("vehicle.description");
        }

        @Override
        public URL getURL() {
                try {
                        return new URL("https://code.google.com/p/ria-scip/");
                } catch (MalformedURLException e) {
                        return null;
                }
        }
}