package com.hacktics.vehicle.dejavu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

import com.hacktics.vehicle.ZAP;

public final class Utils {

	/**
	 * @param args
	 */
	
	public enum CachingSites
	{
		WayBackMachine, ArchiveIt
	}
	
	private static int _sYear;
	private static int _eYear;
	private static String _Site;
	private static Boolean _isOriginalToArchive;
	private static ArrayList<CachingSites> _SupportedCachingSites;
	private static Boolean _isActive;
	private static HashMap<String,String> cachedUrls;
	private static ArrayList<String> _ArrayExtensions;
	private static Boolean _Notification = false;
	private static ArrayList<String> _HistoryOriginalLinks = null;
	private static HashMap<CachingSites,ArrayList<String>> _HistoryCachedLinks = null;
	private static ArrayList<String> _CachedLinksArray = null;
	private static ArrayList<URLObject> URLs = null;
	
	
	public static void addURLs(URLObject url) {
		if (URLs == null)
			URLs = new ArrayList<URLObject>();
		
		if (url!=null)
			URLs.add(url);
	}
	
	public static ArrayList<URLObject> getURLs() {
		if (URLs!=null)
			return URLs;
		
		return null;
		
	}
	
	public static HashMap<CachingSites,ArrayList<String>> get_HistoryCachedLinks() {
		if (_HistoryCachedLinks==null)
			_HistoryCachedLinks = new HashMap<CachingSites,ArrayList<String>>();
		return _HistoryCachedLinks;
	}
	
	public static void set_HistoryCachedLinks(CachingSites site, String link) {
		if (_HistoryCachedLinks==null){
			_HistoryCachedLinks = new HashMap<CachingSites,ArrayList<String>>();
		}
		
		if (get_HistoryCachedLinks().get(site)!=null)
			get_HistoryCachedLinks().get(site).add(link);
		else
		{
			_CachedLinksArray = new ArrayList<String> ();
			_CachedLinksArray.add(link);
			get_HistoryCachedLinks().put(site, _CachedLinksArray );
		}
	}
	
	public static void ResetHistory()
	{
		_CachedLinksArray = null;
		_HistoryOriginalLinks = null;
		_HistoryCachedLinks = null;
	}
	
	
	public static ArrayList<String> get_HistoryOriginalLinks() {
		if (_HistoryOriginalLinks==null)
			_HistoryOriginalLinks = new ArrayList<String>();
		return _HistoryOriginalLinks;
	}
	
	public static void set_HistoryOriginalLinks(
			String link) {
		if (_HistoryOriginalLinks==null)
			_HistoryOriginalLinks = new ArrayList<String>();
		Utils._HistoryOriginalLinks.add(link);
	}
	
	public static ArrayList<String> get_ArrayExtensions() {
		if (_ArrayExtensions==null)
			_ArrayExtensions = new ArrayList<String>();
		return _ArrayExtensions;
	}
	
	public static void set_Notification(Boolean _Notification) {
		Utils._Notification = _Notification;
	}
	
	public static Boolean get_Notification() {
		return _Notification;
	}
	
	public static void set_ArrayExtensions(ArrayList<String> _ArrayExtensions) {
		Utils._ArrayExtensions = _ArrayExtensions;
	}

	public static void setCachedUrls(HashMap<String, String> cachedUrls) {
		Utils.cachedUrls = cachedUrls;
	}
	
	public static HashMap<String, String> getCachedUrls() {
		if (cachedUrls==null)
			cachedUrls = new HashMap<String,String>();
		return cachedUrls;
	}
	public static void set_SupportedCachingSites(ArrayList<CachingSites> _SupportedCachingSites) {
		Utils._SupportedCachingSites = _SupportedCachingSites;
	}
	
	public static ArrayList<CachingSites> get_SupportedCachingSites() {
		return _SupportedCachingSites;
	}
	
	public static void set_eYear(int eYear) {
		_eYear = eYear;
	}
	
	public static int  get_eYear() {
		return _eYear;
	}
	
	public static void set_sYear(int sYear) {
		_sYear = sYear;
	}
	
	public static int  get_sYear() {
		return _sYear;
	}
	
	public static void set_Site(String Site) {
		_Site = Site;
	}
	
	public static String get_Site() {
		return _Site;
	}
	
	public static void set_isOriginalToArchive(Boolean _isOriginalToArchive) {
		Utils._isOriginalToArchive = _isOriginalToArchive;
	}
	
	public static Boolean get_isOriginalToArchive() {
		return _isOriginalToArchive;
	}
	
	public static Boolean get_isActive() {
		return _isActive;
	}
	
	public static void set_isActive(Boolean _isActive) {
		Utils._isActive = _isActive;
	}
	
	
	public Utils()
	{
		//ArrayList<CachingSites> CacheSites = new ArrayList<CachingSites>();
		//CacheSites.add(CachingSites.WayBackMachine);
		//Configuration(2005, 2006, "", true, CacheSites, false);
	}
	
	public static Boolean ExtensionExcluded(String url)
	{
		String[] ext = new String[] {"jpg","gif","js","css", "png","ico","swf" };
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URIException e) {
			return false;
		}
		for (String ex : ext)
		{
			try {
				if (uri.getPath().toLowerCase().contains(ex) || uri.toString().toLowerCase().endsWith(ex) )
					return false;
			} catch (URIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	
	public static void Configuration(int sYear, int eYear, String Site, Boolean isOriginalToArchive, ArrayList<CachingSites> SupportedCachingSites, Boolean isActive)
	{
		set_eYear(eYear);
		set_sYear(sYear);
		set_Site(Site);
		set_isOriginalToArchive(isOriginalToArchive);
		set_SupportedCachingSites(SupportedCachingSites);
		set_isActive(isActive);
	
	}
	
	public static boolean CheckIfLinkExists(String _site, String method)
	{
		HttpMessage _Request = null;
		try {
				URI site = new URI(_site ,false);
				//new HttpRequestHeader(method, site, "1.1")
				_Request = new HttpMessage(site);
				//_Request.setRequestHeader(new HttpRequestHeader(method, site,"1.1"));
				ZAP.send(_Request);
		} catch (URIException | HttpMalformedHeaderException e1) {
			//e1.printStackTrace();
		}
		
		if (_Request.getResponseHeader()!=null)
		{
			if (_Request.getResponseHeader().getStatusCode() == 200)
				return true;
		}
		return false;
	}
	
	
	//Modify links convention to return a modified array of links
	public static ArrayList<String> ModifyLinksConvention(String OriginalSite, ArrayList<String> CachedPages)
	{
		ArrayList<String> ModifiedArray = new ArrayList<String>();
		
		if (CachedPages!=null)
		{
			for (String cache : CachedPages)
			{
				if (cache!=null)
				{
					String mlink = ModifyLinkConvention (OriginalSite, cache);
					if (mlink!=null)
						ModifiedArray.add(mlink);
				}
			}
		}
		
		return ModifiedArray;
	}
	
	//Modify link convention in order to take care of "a.html, /a.html, http://<host>/a.html to return a modified single link
	public static String CorrectURL(String OriginalSite, String CachedPage)
	{
		URL OriginalUrl = null;
		URL cachedUrl = null;
		String OriginalHost = null;
		String OriginalPath = null;

		String CachedHost = null;
		String CachedlPath = null;
		
		CachedPage = CachedPage.replace("'", "");
		CachedPage = CachedPage.replace("\"", "");
		CachedPage = CachedPage.replace("../", "");
		
		try {
			cachedUrl = new URL(CachedPage);
			
		} catch (MalformedURLException e) {
		}
		
		try {
			OriginalUrl = new URL(OriginalSite);
			
		} catch (MalformedURLException e) {
		}


		if (cachedUrl!=null)
		{
			CachedHost = cachedUrl.getHost();
			CachedlPath = cachedUrl.getPath();
			OriginalHost = OriginalUrl.getHost();
			OriginalPath = OriginalUrl.getPath();
	
			if (CachedHost.toLowerCase().toString().contains(OriginalHost.toLowerCase().toString()) || OriginalHost.toLowerCase().toString().contains(CachedHost.toLowerCase().toString()))
			{
					return cachedUrl.toString();
			}
			else
			{
					//check for subdomains
					String[] cHost = CachedHost.split("\\.");
					String[] oHost = OriginalHost.split("\\.");
					
					if (cHost.length > 2)
					{
						CachedHost = cHost[cHost.length -3].toString() + "." + cHost[cHost.length -2].toString() + "." + cHost[cHost.length-1].toString();
					}
					else if (cHost.length == 2)
					{
						CachedHost = cHost[cHost.length -2].toString() + "." + cHost[cHost.length-1].toString();
					}
					
					if (oHost.length > 2)
					{
						OriginalHost = oHost[oHost.length -3].toString() + "."+  oHost[oHost.length -2].toString() + "." + oHost[oHost.length-1].toString();
					}
					else if (cHost.length == 2)
					{
						OriginalHost = oHost[oHost.length -2].toString() + "." + oHost[oHost.length-1].toString();
					}
					
					if (cHost.length > 1 && oHost.length > 1 && OriginalHost.contains(Utils.get_Site()))
					{
						if (cHost.toString().contains(oHost.toString()))
							return cachedUrl.toString();
					}
			}
				
		}
		else
		{
			if (CachedPage.startsWith("/") && !CachedPage.contains(OriginalUrl.getHost()))
			{
				//System.out.println(OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + CachedPage);
				return OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + CachedPage;

			}
			
			if (CachedPage.startsWith("/") && CachedPage.contains(OriginalUrl.getHost()))
			{
				//System.out.println(OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + StringUtils.remove(CachedPage,OriginalUrl.getHost()));
				return  OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + StringUtils.remove(CachedPage,OriginalUrl.getHost());
			}
			
			if (!CachedPage.startsWith("/") && !CachedPage.startsWith("http://") && !CachedPage.contains(":") && !CachedPage.contains(OriginalUrl.getHost()) && CachedPage.contains(".") || CachedPage.endsWith("/"))
			{
				//System.out.println(OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + "/" + CachedPage);
				return  OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + "/" + CachedPage;
			}
			
			if (!CachedPage.startsWith("/") && CachedPage.contains(OriginalUrl.getHost()) )
			{
				//System.out.println(OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + "/" + StringUtils.remove(CachedPage,OriginalUrl.getHost()));
				return  OriginalUrl.getProtocol() + "://" + OriginalUrl.getHost() + "/" + StringUtils.remove(CachedPage,OriginalUrl.getHost());
			}
		}

		return null;

	}
	public static String ModifyLinkConvention(String OriginalSite, String CachedPage)
	{
		URL NewUrl;
		String newUrl = CorrectURL(OriginalSite,CachedPage);
		if (newUrl!=null)
		{

			try {
				NewUrl = new URL(newUrl);
				if (NewUrl!=null && NewUrl.getHost().contains(Utils.get_Site()))
						return NewUrl.toString();
				
			} catch (MalformedURLException e) {

			}
		
		}
		
		return null;
		
	}
}
