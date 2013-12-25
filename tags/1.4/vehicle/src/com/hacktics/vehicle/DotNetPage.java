/**
 * 
 */
package com.hacktics.vehicle;

import java.util.HashMap;
import java.util.List;

import com.hacktics.viewstate.ViewState;

/**
 * @author alex.mor
 *
 */
public class DotNetPage {
	private String url;
	private String response;
	private HashMap<String,List<String>> controlEvents;
	private ViewState viewstate;
	
	/**
	 * 
	 * @return page URL
	 */
	public String getUrl() {
		return url;
	}

	private void setUrl(String url) {
		this.url = url;
	}
	
	public String getResponse() {
		return response;
	}

	private void setResponse(String response) {
		this.response = response;
	}
	/**
	 * 
	 * @return control names and their corresponding events
	 */
	public HashMap<String,List<String>> getControlEvents() {
		return controlEvents;
	}
	
	public void addControlEvents(HashMap<String,List<String>> controlEvents) {
		this.controlEvents.putAll(controlEvents);
	}
	
	
	public ViewState getViewState() {
		return viewstate;
	}

	public void setViewState(ViewState viewstate) {
		this.viewstate = viewstate;
	}
	
	/**
	 * @param url
	 * @param response
	 * @param controlEvents
	 */
	public DotNetPage(String url,String response,HashMap<String,List<String>> controlEvents) {
		this.setUrl(url);
		this.setResponse(response);
		this.controlEvents = controlEvents;
		this.setViewState(new ViewState(response));
	}








}
