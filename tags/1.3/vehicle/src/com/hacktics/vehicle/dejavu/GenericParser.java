package com.hacktics.vehicle.dejavu;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericParser {

	ArrayList<String> Links;
	
	public void addLink(String link) {
		if (Links==null)
			Links = new ArrayList<String>();
		Links.add(link);
	}
	
	public ArrayList<String> getLinks() {
		return Links;
	}
	
	public ArrayList<String> getModifiedLinks(String Url, String _Response, Boolean HistoryCheck)
	{
		String regex = "";
		regex = "<a\\s?[^ >]href\\s?=\\s??([^ >]+)[^>]*>";
		regex = "<.+href=([^ >]+)";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(_Response);
		
        	
	        while (matcher.find()) {
		      	  try {
		      		  String matcherStr = (String) matcher.group(1);
		      		  if (getLinks()==null || (getLinks()!=null && !getLinks().contains(matcherStr)))
		      		  {
		      			  String link = Utils.ModifyLinkConvention(Url, matcherStr);
		      			  if (link!=null)
		      			  {
		      						  addLink(link);
		      			  }
		      		  }
		      	  }
	      		  catch (Exception e){

	      		  }
	          	  
	      	}
	        
	        
	        //form action
			regex = "<form\\s?[^ >]?action\\s?=\\s?([^ >]+)[^>]*>";
	        pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
	        matcher = pattern.matcher(_Response);
			
	        	
		        while (matcher.find()) {
			      	  try {
			      		  String matcherStr = (String) matcher.group(1);
			      		  if (getLinks()==null || (getLinks()!=null && !getLinks().contains(matcherStr)))
			      		  {
			      			  String link = Utils.ModifyLinkConvention(Url, matcherStr);
			      			  if (link!=null)
			      			  {
			      						  addLink(link);
			      			  }
			      		  }
			      	  }
		      		  catch (Exception e){

		      		  }
		          	  
		      	}

        return getLinks();
	}
	
	
	public ArrayList<String> getLinksInPage(String _Response)
	{
		
		String regex = "<a\\s[^>]*href=([^>]*?)[^>]*>";
		regex = "<a\\s?href\\s?=\\s??([^ >]+)[^>]*>";
		regex = "<.+href=([^ >]+)";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(_Response);

        while (matcher.find()) {
      	  try {
      		  String matcherStr = (String) matcher.group(1);
      		  if (getLinks()==null || (getLinks()!=null && !getLinks().contains(matcherStr)))
      		  {
      			  if (matcherStr!=null)
      				  addLink(matcherStr);
      		  }
      	  }
      	  catch (Exception e){

      	  }
        }
        
		regex = "<form\\s?[^ >]?action\\s?=\\s?([^ >]+)[^>]*>";
        pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
        matcher = pattern.matcher(_Response);
		
        	
        while (matcher.find()) {
        	  try {

        		  String matcherStr = (String) matcher.group(1);
        		  if (getLinks()==null || (getLinks()!=null && !getLinks().contains(matcherStr)))
        		  {
        			  if (matcherStr!=null)
        				  addLink(matcherStr);

        		  }
        	  }
        	  catch (Exception e){

        	  }
          }
        
        
        return getLinks();
	}

}
