/**
 * 
 */
package com.hacktics.scip;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.parosproxy.paros.network.HttpMessage;

import com.hacktics.scip.gui.SCIPView;
/**
 * @author alex.mor
 *
 */
public class SCIP {

	private static List<String> ignoreList = new ArrayList<String>(Arrays.asList("__VIEWSTATE","__LASTFOCUS","__EVENTVALIDATION","__EVENTTARGET","__EVENTARGUMENT"));
	/**
	 * 
	 */
	public SCIP(final HttpMessage hm) {	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				SCIPView gui = new SCIPView(hm);
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
		String regex = "<(input|textarea|button).*?name=\"(.*?)\".*"+attribute+".*?/>";
		
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
	 * @param 
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


}
