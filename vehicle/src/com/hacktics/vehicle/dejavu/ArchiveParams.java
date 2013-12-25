package com.hacktics.vehicle.dejavu;

import java.util.ArrayList;

import org.parosproxy.paros.network.HttpMessage;

public class ArchiveParams {

		private HttpMessage message;
		private ArrayList<String> links;
		private String year;
		private String url;
		
		
		public String getUrl() {
			return url;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setYear(String year) {
			this.year = year;
		}
		
		public String getYear() {
			return year;
		}
		
		public void setLinks(ArrayList<String> links) {
			this.links = links;
		}
		
		public ArrayList<String> getLinks() {
			return links;
		}
		
		public void setMessage(HttpMessage message) {
			this.message = message;
		}
		
		public HttpMessage getMessage() {
			return message;
		}
		
		
}
