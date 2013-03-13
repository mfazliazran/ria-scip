
/*

 */

package org.zaproxy.zap.extension.scip;

import com.hacktics.scip.SCIP;

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
public class SCIPExtension extends ExtensionAdaptor {

    private JMenuItem mnuControlEvents = null;
    private RightClickSCIP popupMsgControlEvents = null;
    private ResourceBundle messages = null;
    
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public static final String NAME = "SCIP"; 
  
	    public SCIPExtension() {
	        super();
	        initialize();
	
	    }
	
	    /**
	     * @param name
	     */
	    public SCIPExtension(String name) {
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
                extensionHook.getHookMenu().addToolsMenuItem(getmnuSCIP());
        	    	// Register our popup menu item, as long as we're not running as a daemon
        	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupControlEvents());
        	    }
        }


        private JMenuItem getmnuSCIP() {
        if (mnuControlEvents == null) {
                mnuControlEvents = new JMenuItem();
                mnuControlEvents.setText(messages.getString("scip.menu"));

                mnuControlEvents.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                        // This is where you do what you want to do.
                        // In this case we'll just show a popup message.
                	try {
	                		new SCIP(null);
	                		logger.info("SCIP Loaded");
	                	}
	                	catch (Exception ex) {
	                		logger.error(ex.getMessage(), ex);
	                	}
                	}
                });
        	}
        	return mnuControlEvents;
        }
        
        private RightClickSCIP getPopupControlEvents() {
    		if (popupMsgControlEvents  == null) {
    			popupMsgControlEvents = new RightClickSCIP(messages.getString("scip.menu.rightclick"));
    			popupMsgControlEvents.setExtension(this);
    		}
    		return popupMsgControlEvents;
    	}

        @Override
        public String getAuthor() {
                return messages.getString("scip.author");
        }

        @Override
        public String getDescription() {
                return messages.getString("scip.description");
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