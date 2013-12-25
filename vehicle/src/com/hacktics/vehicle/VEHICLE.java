/**
 * 
 */
package com.hacktics.vehicle;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.parosproxy.paros.network.HttpMessage;

import com.hacktics.vehicle.gui.VehicleView;
import com.hacktics.viewstate.ViewState;
/**
 * @author alex.mor
 *
 */
public class VEHICLE {

	private static List<String> ignoreList = new ArrayList<String>(Arrays.asList("__VIEWSTATE","__LASTFOCUS","__EVENTVALIDATION","__EVENTTARGET","__EVENTARGUMENT"));
	/**
	 * 
	 */
	public VEHICLE(final HttpMessage hm) {	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				VehicleView gui = new VehicleView(hm);
				gui.createShowGUI();
				
			}
		});
	}
	
	/**
	 * @param response raw response body
	 * @return
	 */
	public static List<String> getControlsName(String response,String attribute) {
		List<String> matchedRegex = new ArrayList<String>();
		//maybe add more tags? form, button etc.
		String regex = "<(input|textarea|button).*?name=\"(.*?)\".*"+attribute+".*?>";
		
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
      	  try {
      		  	if (!ignoreList.contains(matcher.group(2)))
            	matchedRegex.add(matcher.group(2));
      	  }
      	  catch (Exception e){
      		System.out.println(e.toString());
      	  }
        }
		
		return matchedRegex;
	}
	
	/**
	 * @param response raw response body
	 * @return
	 */
	public static List<String> getComments(String response) {
		List<String> matchedRegex = new ArrayList<String>();
		//maybe add more tags? form, button etc.
		String regex = "<!--(.*?)-->";
		
		//include newline in the dot operator (DOTALL)
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        while (matcher.find()) {
      	  try {
            	matchedRegex.addAll(getControlsName(matcher.group(1),""));
      	  }
      	  catch (Exception e){
      		System.out.println(e.toString());
      	  }
        }
		
		return matchedRegex;
	}
		
	/**
	 * @param Response	raw response body
	 * @param ControlName	the page control whos events we are interested in
	 */
	public static List<String> getControlEvents(String Response,String ControlName) {
		List<String> matchedRegex = new ArrayList<String>();
		String regex = "name=\"" + Pattern.quote(ControlName) + "\" .*?/>";
		
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(Response);
        
        if (matcher.find()) {
      	  try {
      		Pattern eventPattern = Pattern.compile(" (on.*?)=",Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher eventMatcher = eventPattern.matcher(matcher.group(0));
            while (eventMatcher.find()) { 
            	matchedRegex.add(eventMatcher.group(1));
            }
      	  }
      	  catch (Exception e){
      		System.out.println(e.toString());
      	  }
        }
		
		return matchedRegex;
	}
	
	/**
	 * 
	 * @param controlName	the control's name that we are executing
	 * @param viewstate		the page's viewstate object
	 * @return
	 */
	public static String getRequest(String controlName,ViewState viewstate) {
		String stringRequest = "";
		
		if (controlName!=null && !viewstate.getViewStateText().equals("")) {

			String stringEventValidation = viewstate.getEventValidation();
			
			//debug
			/*
			System.out.println("Viewstate Map= " + dotNetPage.getViewState().getViewStateMap());
			System.out.println("Viewstate Base64= " + dotNetPage.getViewState().getViewStateText());
			System.out.println("EV Map= " + dotNetPage.getViewState().getEventValidationMap());
			System.out.println("EV Base64= " + dotNetPage.getViewState().getEventValidation());
			*/
			String stringViewState = viewstate.getViewStateText();
			
			try {
				String stringViewStateParam = "";
				if (viewstate.isSplit()) {
					int i=1;
					if (viewstate.isSplitNoCount()) {
						String[] splitViewStateParam = stringViewState.split("(?<=\\G.{1000})");
						for (String splitParam : splitViewStateParam) {
							stringViewStateParam = stringViewStateParam + "&__VIEWSTATE" + (i++) + "=" + splitParam;

						}
						stringViewStateParam = stringViewStateParam + "&__VIEWSTATE0=" + --i + "&VIEWSTATE=";
					}
					else {
						
					}
				}
				else {
					stringViewStateParam = "&__VIEWSTATE="+URLEncoder.encode(stringViewState,"UTF-8");
				}
				stringRequest = "__EVENTTARGET="+controlName+"&__EVENTARGUMENT=&__EVENTVALIDATION="+URLEncoder.encode(stringEventValidation,"UTF-8") 
						+ stringViewStateParam;
										
				if (viewstate.isEncrypted()) {
					stringRequest += "&__VIEWSTATEENCRYPTED=";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return stringRequest;
}


}
