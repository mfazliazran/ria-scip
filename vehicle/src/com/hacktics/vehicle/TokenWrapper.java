package com.hacktics.vehicle;

public class TokenWrapper {

	String startStr;
	String tokenStr;
	String endStr;
	
	public TokenWrapper(String startStr, String tokenStr, String endStr) {
		super();
		this.startStr = startStr;
		this.tokenStr = tokenStr;
		this.endStr = endStr;
	}
	
	public String getStartStr() {
		return startStr;
	}
	
	public void setStartStr(String startStr) {
		this.startStr = startStr;
	}
	
	public String getTokenStr() {
		return tokenStr;
	}
	
	public void setTokenStr(String tokenStr) {
		this.tokenStr = tokenStr;
	}
	
	public String getEndStr() {
		return endStr;
	}
	
	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}
}
