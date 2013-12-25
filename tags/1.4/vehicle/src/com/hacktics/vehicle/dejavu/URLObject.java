package com.hacktics.vehicle.dejavu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class URLObject {


	private String _url;
	private Boolean _isCached;
	private String _Response;
	private int _sYear;
	private int _eYear;
	private CacheObject _CacheObject;
	private ArrayList<Utils.CachingSites> _SupportedCachingSites;
	private Boolean _isMainToArchive;
	
	
	public void set_CacheObject(CacheObject _CacheObject) {
		this._CacheObject = _CacheObject;
	}
	
	public CacheObject get_CacheObject() {
		return _CacheObject;
	}

	public String get_URL() {
		return _url;
	}
	
	public void set_URL(String _url) {
		this._url = _url;
	}
	
	public Boolean get_IsCached() {
		return _isCached;
	}
	
	public void set_IsCached(Boolean _isCached) {
		this._isCached = _isCached;
	}
	
	public String get_Response() {
		return _Response;
	}
	
	public void set_Response(String _PageContent) {
		this._Response = _PageContent;
	}
	

	public URLObject (String _site, String _Response)
	{
		this.set_URL(_site);
		this.set_Response(_Response);
		CacheObject cache = new CacheObject(this);
		set_CacheObject(cache);
		Utils.addURLs(this);
	}
	
	public String getSiteContent()
	{
		if (get_Response()!=null)
			return get_Response();
		
		return null;
	}
	
	public ArrayList<String> getSiteLinks()
	{
		GenericParser genParser = new GenericParser();
		return genParser.getModifiedLinks(this.get_URL(), this.get_Response(), true);
	}
	

}
