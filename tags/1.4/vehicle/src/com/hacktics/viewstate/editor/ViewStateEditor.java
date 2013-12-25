package com.hacktics.viewstate.editor;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.hacktics.payloaddb.PayloadDB;
import com.hacktics.viewstate.ViewState;
import com.hacktics.viewstate.editor.DynamicTree;


 
public class ViewStateEditor extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6020788471637405730L;
	private int newNodeSuffix = 1;
    private final String ADD_COMMAND = "add";
    private final String REMOVE_COMMAND = "remove";
    private final String APPLY_COMMAND = "apply";
    private final String ADD_NEW_CONTROL_COMMAND = "addNewControl";
    private final String ADD_DIFERENT_DATABASE_COMMAND = "addDB";
    private final String TOGGLE_BOOLEAN_COMMAND = "toggleBoolean";
    //private JTextField methodNameTextBox;
     
    private DynamicTree treePanel;
    private ViewState vs;
    private boolean readOnly;
    
    public ViewState getViewState() {
    	return vs;
    }

 
    public ViewStateEditor(ViewState vs,boolean readOnly) {
    	this.vs = vs;
    	this.readOnly = readOnly;
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	initComponents();
		    	buildViewStateTree(treePanel);
		    }
		});
         
        
    }
   
    
    public void buildViewStateTree(DynamicTree treePanel) {
        
    	//System.out.println(vs.getViewState());
    	Map<String,Object> map = vs.getViewStateMap();
    	//System.out.println(map);
        	
    	
    	DefaultMutableTreeNode TreeNode = treePanel.rootNode;
    	
    	//TreeNode = MytreePanel.addObject(null,TreeNode);
    	TreeNode = OpenMap(TreeNode,(Map<String, Object>) map,false);
    	
    	//remove the "Root Node" node:
    	treePanel.treeModel.setRoot((DefaultMutableTreeNode) treePanel.rootNode.getFirstChild());
    	treePanel.rootNode = (DefaultMutableTreeNode) treePanel.rootNode.getFirstChild();
    	
    	//collapse entire tree
    	for (int i = 0; i < treePanel.tree.getRowCount(); i++) {
    		treePanel.tree.expandRow(i);
    	}
    	
    	
      }
     

    
    public DefaultMutableTreeNode OpenMap(DefaultMutableTreeNode tree, Map<String, Object> current,boolean isArrayList)
    {
    	Iterator<Entry<String, Object>> it=current.entrySet().iterator();
        
    	String key=null;
    	Map<String, Object> value=null;
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    	while(it.hasNext())
        {
            Map.Entry<String, Object>  m =it.next();
              key=m.getKey().toString().substring(5);
              if (!isArrayList) {

            	  root = new DefaultMutableTreeNode(key);
            	  root = treePanel.addObject(tree,root);
            	  
            	  //color indicator
            	  
            	  if (key.equals("ArrayList")) {
            		  ArrayList<HashMap<String,Object>> arrayList = (ArrayList<HashMap<String,Object>>)m.getValue();
            		  boolean gotInt = false;
            		  boolean gotPair = false;
            		  boolean gotDataBound = false;
            		  if (arrayList.size()>1) {
            			  for (HashMap<String,Object> arrayValue : arrayList) {
            				  if (arrayValue.containsKey("Type_Int32")) {
            					  gotInt = true;
            				  }
            				  if (arrayValue.containsKey("Type_Pair")) {
            					  gotPair = true;
            				  }
            				  if (arrayValue.containsValue("_!DataBound")) {
            					  gotDataBound = true;
            				  }
            			  }
            		  }
            		  if (gotInt && gotPair) {
                		  root.setUserObject("<html><font color=\"Red\"><b>"+root.getUserObject()+"</b></font></html>");
                	  }  
            		  if (gotDataBound) {
                		  root.setUserObject("<html><font color=\"Blue\"><b>"+root.getUserObject()+"</b></font></html>");
                	  }
            	  }
            	  
              }
              
              if (isArrayList && m.getValue().getClass().equals(ArrayList.class)) {           	  
            	  Iterator<?> itArrayList = ((ArrayList<?>)m.getValue()).iterator();
            	  while(itArrayList.hasNext())
                  {
                      Map<String, Object> arrayHashMap = (Map<String, Object>)itArrayList.next();
                      root = OpenMap(tree,arrayHashMap,false);
                  }
            	  return root;
              }
              
              if (m.getValue() == null) {
            	  root = treePanel.addObject(root,"null");
            	                }
              else if (m.getValue().getClass().equals(Map.class)) {
            	 
            	  value =(Map<String, Object>) m.getValue();
                  root = OpenMap(root,value,false);
              }
              else if (m.getValue().getClass().equals(ArrayList.class))
              {
            	  value = new HashMap<String, Object>();
            	  value.put("ArrayList", (List)m.getValue());
            	  root = OpenMap(root,value,true);
              }
              else {
            	  root = treePanel.addObject(root,m.getValue());
              }                     
        }  		
            if(key != null && key.length() == 0)
            {
            	return new DefaultMutableTreeNode("");
            }
		return 	root;
    }
     
    public Map<String, Object> BuildMap(DefaultMutableTreeNode tree)
    {
    	Map<String, Object> treeMap = new HashMap<String, Object>();
    	List<Object> stringListArray = new ArrayList<Object>();
    	
    	String key = "Type_" + tree.getUserObject().toString();
    	//fix for indicator
    	if (key.contains("ArrayList")) {
    		key = "Type_ArrayList";
    	}

    	if (tree.getChildCount()>0) {
    		if (tree.getChildCount()>1 || tree.getChildAt(0).getChildCount()>0) {
        		Enumeration<?> treeEnumeration = tree.children();
        		while(treeEnumeration.hasMoreElements())
        		{
        			DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeEnumeration.nextElement();
        			stringListArray.add(BuildMap((DefaultMutableTreeNode) child));
        		}
        		treeMap.put(key, stringListArray);
        	}
    		else{
        		String value = tree.getFirstChild().toString();
        		treeMap.put(key, value);
        	}
    	}
    	else {
    		treeMap.put(key, new ArrayList<HashMap<String,Object>>());
    	}
    	

    	return treeMap;
    }


    

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
        case ADD_COMMAND:
        	System.out.println("ok");
        	break;
        }
         
        if (ADD_COMMAND.equals(command)) {
            //Add button clicked
            treePanel.addObject("New Node " + newNodeSuffix++);
        } else if (REMOVE_COMMAND.equals(command)) {
            //Remove button clicked
            treePanel.removeCurrentNode();
        } else if (APPLY_COMMAND.equals(command)) {
        	if (!readOnly) {
        		//rebuild hashmap from tree
            	Map<String,Object> newMap = BuildMap((DefaultMutableTreeNode) treePanel.rootNode);
            	//System.out.println(newMap);
            	
            	vs.setViewStateMap(newMap);
        	}
        	
        	
			//http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
			//WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		    //Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			dispose(); 
           
        }
        // adding a new button to the viewState - Michal Added
        else if (ADD_NEW_CONTROL_COMMAND.equals(command))
        {
           	addNewControl();
        }
        else if (ADD_DIFERENT_DATABASE_COMMAND.equals(command))
        {
        	addDataSourceID();
        }
        else if (TOGGLE_BOOLEAN_COMMAND.equals(command)) {
        	toggleBoolean();
        }
    }
    
    private void addNewControl()
    {   		
	   if (treePanel.tree.isSelectionEmpty()) return;
	   DefaultMutableTreeNode treeSelection = (DefaultMutableTreeNode) treePanel.tree.getSelectionPath().getLastPathComponent();
	   if (treeSelection.toString().contains("ArrayList")) {
		   int lastInt32 = 1;
		   if (treeSelection.getChildCount()>1) {
			   TreeNode nodeInt32 = (DefaultMutableTreeNode)treeSelection.getChildAt(treeSelection.getChildCount()-2);
			   if (nodeInt32.toString().equals("Int32")) {
				   lastInt32 = Integer.parseInt(nodeInt32.getChildAt(0).toString()) + 2;
			   }   
				   //add the Int32 node
				   DefaultMutableTreeNode int32 = treePanel.addObject("Int32");
				   treePanel.addObject(int32, lastInt32);
				   
				   //add the rest (the Pair node) from XML file
				   DefaultMutableTreeNode btnNode = BuildTreeFromXMLFile("newControl.xml");
			   
		   }    
	   }
    }
    
    private void toggleBoolean() {
    
    	if (treePanel.tree.isSelectionEmpty()) return;
 	   	DefaultMutableTreeNode treeSelection = (DefaultMutableTreeNode) treePanel.tree.getSelectionPath().getLastPathComponent();

 	   	if (treeSelection.toString().equals("False")) {
 	   		if (treeSelection.getChildCount()==1) {
 	   			treeSelection.setUserObject("True");
 	   			((DefaultMutableTreeNode)treeSelection.getFirstChild()).setUserObject("True");
 	   			treePanel.tree.repaint();
 		   }
 	   }
 	   	else if (treeSelection.toString().equals("True")) {
 	   	if (treeSelection.getChildCount()==1) {
 	   			treeSelection.setUserObject("False");
 	   			((DefaultMutableTreeNode)treeSelection.getFirstChild()).setUserObject("False");
 	   			treePanel.tree.repaint();
		   }
 	   	}
    }
    
    private DefaultMutableTreeNode BuildTreeFromXMLFile(String filename)
    {
        Document d;
        DefaultMutableTreeNode root = null;

        try{

            d = PayloadDB.getDocumentBuilder(filename, getClass().getResource("/com/hacktics/viewstate/resource/controls/"+filename));
            Element e = d.getDocumentElement();

            if(e.hasChildNodes()){
                root = new DefaultMutableTreeNode(e.getTagName());
                DefaultMutableTreeNode rootNode = treePanel.addObject(root);
                NodeList children = e.getChildNodes();
                for(int i=0;i<children.getLength();i++){
                    Node child = children.item(i);
                    childBuild(child,root,rootNode);
                }
            }
            
        }catch(ParserConfigurationException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.toString());
        }catch(SAXException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.toString());
        }catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.toString());
        }
        
        return root;
    }
    
    private void childBuild(Node child,DefaultMutableTreeNode parent, DefaultMutableTreeNode parentNode){
        short type = child.getNodeType();
        if(type == Node.ELEMENT_NODE){
            Element e = (Element)child;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(e.getTagName());
            parent.add(node);
          //----------------------------------------------------------
            DefaultMutableTreeNode parentNodeAgain = treePanel.addObject(parentNode, node);
          //----------------------------------------------------------
            if(e.hasChildNodes()){
                NodeList list = e.getChildNodes();
                for(int i=0;i<list.getLength();i++){
                    childBuild(list.item(i),node,parentNodeAgain);
                }
            }

        }else if(type == Node.TEXT_NODE){
            Text t = (Text)child;
            String textContent = t.getTextContent();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                    textContent);
            parent.add(node);
            treePanel.addObject(parentNode, node);
        }
    }
  
    private void addDataSourceID()
    {   		
 	   if (treePanel.tree.isSelectionEmpty()) return;
 	   DefaultMutableTreeNode treeSelection = (DefaultMutableTreeNode) treePanel.tree.getSelectionPath().getLastPathComponent();
 	   if (treeSelection.toString().contains("ArrayList")) {
		   //add the IndexedString
		   DefaultMutableTreeNode indexedString = treePanel.addObject("IndexedString");
		   treePanel.addObject(indexedString, "DataSourceID");
		   
		   //add String
		   DefaultMutableTreeNode s = treePanel.addObject("String");
		   treePanel.addObject(s, "[USER INPUT]");
 	   }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void initComponents() {
                
        treePanel = new DynamicTree();  
                
        JButton addButton = new JButton("Add");
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);
        addButton.setEnabled(!readOnly);
         
        JButton removeButton = new JButton("Remove");
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);
        removeButton.setEnabled(!readOnly);
         
        JButton applyButton = new JButton("Apply");
        applyButton.setActionCommand(APPLY_COMMAND);
        applyButton.addActionListener(this);
        
        JButton addNewControl = new JButton("Add Control");
        addNewControl.setActionCommand(ADD_NEW_CONTROL_COMMAND);
        addNewControl.addActionListener(this);
        addNewControl.setEnabled(!readOnly);
        
        JButton addDB = new JButton("Add SQL Data Source");
        addDB.setActionCommand(ADD_DIFERENT_DATABASE_COMMAND);
        addDB.addActionListener(this);
        addDB.setEnabled(!readOnly);
        
        
        JButton toogleBoolean = new JButton("Toggle Boolean");
        toogleBoolean.setActionCommand(TOGGLE_BOOLEAN_COMMAND);
        toogleBoolean.addActionListener(this);
        toogleBoolean.setEnabled(!readOnly);
 
        if (!readOnly) {
        	addNewControl.setForeground(Color.RED);
        	addDB.setForeground(Color.BLUE);
        }
        else {
              	applyButton.setText("Close"); 	
        }
        
        //Lay everything out.
        treePanel.setPreferredSize(new Dimension(500, 500));
        add(treePanel, BorderLayout.CENTER);
 
        JPanel panel = new JPanel(new GridLayout(2,3));
       
        panel.add(addDB);
        panel.add(addNewControl);
        panel.add(toogleBoolean);
        panel.add(addButton);
        panel.add(removeButton); 
        panel.add(applyButton);
        
        add(panel, BorderLayout.SOUTH);
        
		//create window
		setTitle("ViewState Editor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500,500);
		
		
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		setResizable(true);
    }
}

