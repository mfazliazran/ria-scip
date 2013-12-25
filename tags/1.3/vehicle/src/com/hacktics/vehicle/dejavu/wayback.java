package com.hacktics.vehicle.dejavu;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

import com.hacktics.vehicle.ZAP;

public class wayback extends GenericParser {

	
	private HttpMessage _Request;
	private String _Response;
	private ArrayList<String> Links;
	private String _modifiedURL = null;
	private String _sourceURL = null;
	private String _Year;
	
	public void set_sourceURL(String _sourceURL) {
		this._sourceURL = _sourceURL;
	}
	
	public String get_sourceURL() {
		return _sourceURL;
	}
	
	public void set_Year(String _Year) {
		this._Year = _Year;
	}
	
	public String get_Year() {
		return _Year;
	}
	
	public void set_modifiedURL(String _getURL) {
		this._modifiedURL = _getURL;
	}
	public String get_modifiedURL() {
		return _modifiedURL;
	}
	
	public void addLink(String link) {
		if (Links==null)
			Links = new ArrayList<String>();
		Links.add(link);
	}
	
	public void setLinks(ArrayList<String> links) {
		Links = links;
	}
	
	public ArrayList<String> getLinks() {
		return Links;
	}
	
	public void set_Request(HttpMessage _Request) {
		this._Request = _Request;
	}
	
	public void set_Response(String _Response) {
		this._Response = _Response;
	}
	
	public HttpMessage get_Request() {
		return _Request;
	}
	
	public String get_Response() {
		return _Response;
	}
	
	
	//Credit: https://github.com/caesar0301/libwayback/blob/master/libcrawler.pyW
	public String getYearByUrl(String url)
	{
		int length = "http://web.archive.org/web/".length();
		String year = url.substring(length,length + 4);
		
		try {
			if (Integer.parseInt(year) > 0 )
				return year;
		} catch (NumberFormatException ex)
		{
			return "";
		}
		return "";
		
	}
	public Boolean GetResponse(String url, String year)
	{
		set_sourceURL(url);
		if (get_sourceURL() == url && get_modifiedURL()!=null && get_Year()==year && get_Response()!=null)
		{
			return true;
		}
		else
		{
			
			String newURL = null;
			newURL = GetRedirectedLink(url,year);
			
			if (newURL!=null)
			{
				set_modifiedURL(newURL);
				set_Year(year);
				
				try {
					URI site = new URI(newURL ,false);
					_Request = new HttpMessage(site);
					ZAP.send(_Request);
				} catch (URIException | HttpMalformedHeaderException e1) {
					//e1.printStackTrace();
				}
				
				if (_Request.getRequestBody()!=null)
				{
					_Response = _Request.getResponseBody().toString();
					set_Response(_Response);
					return true;
				}
			}
		}
		return false;
	}
	public String GetResponseString(String url, String year)
	{
		//set_sourceURL(url);
	
			
			String newURL = null;
			newURL = GetRedirectedLink(url,year);
			
			if (newURL!=null)
			{
		//	set_modifiedURL(newURL);
		//		set_Year(year);
				
				try {
					URI site = new URI(newURL ,false);
					_Request = new HttpMessage(site);
					ZAP.send(_Request);
				} catch (URIException | HttpMalformedHeaderException e1) {
					//e1.printStackTrace();
				}
				
				if (_Request.getRequestBody()!=null)
				{
					_Response = _Request.getResponseBody().toString();
		//			set_Response(_Response);
					return _Response;
				}
			}
		
		return "";
	}
	public String GetRedirectedLink(String url, String year)
	{

			try {
				URI site = new URI("http://web.archive.org/web/" + year + "01id_/" + url ,false);
				_Request = new HttpMessage(site);
				ZAP.send(_Request);
			} catch (URIException | HttpMalformedHeaderException e1) {
				e1.printStackTrace();
			}
			
			if (_Request.getRequestHeader()!=null)
			{
				String newLocation = _Request.getResponseHeader().getHeader("Location");
				
				if (newLocation!=null && newLocation.startsWith("/web") && newLocation.length() > 4)
				{
					return "http://web.archive.org" + newLocation;
				}
			}
			
		return null;
		
	}
	public ArrayList<String> getLinksInPage(String url, String year)
	{
		set_sourceURL(url);
		
		String newURL = null;
		newURL = GetRedirectedLink(url,year);

		if (newURL!=null)
		{
			set_modifiedURL(newURL);
			
			try {
				URI site = new URI(newURL ,false);
				_Request = new HttpMessage(site);
				ZAP.send(_Request);
			} catch (URIException | HttpMalformedHeaderException e1) {
			}
			
			if (_Request.getRequestBody()!=null)
			{
				_Response = _Request.getResponseBody().toString();
				setLinks(super.getLinksInPage(_Response));
			}
		}

		return Links;
	}
}
