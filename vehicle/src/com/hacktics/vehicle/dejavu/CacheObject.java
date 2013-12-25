package com.hacktics.vehicle.dejavu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CacheObject  {
	private ArrayList<String> _HistoryLinks;
	private HashMap<Utils.CachingSites,String> _CachedLinksForLink;
	private HashMap<Utils.CachingSites,ArrayList<String>> _ModifiedCachedLinksFromSite;
	private HashMap<Utils.CachingSites,ArrayList<String>> _MultiCacheLinksFromSite;
	private ArchiveSource archiveSource;
	private ArchiveObject _ArchiveObject;
	private URLObject urlObject;

		
	private String _currentURL;
	private String _currentContent;
	private String _currentYear;
	
	
	public void setArchiveSource(ArchiveSource archiveSource) {
		this.archiveSource = archiveSource;
	}
	
	public ArchiveObject getArchiveSource(Utils.CachingSites caching) {
		if (getArchiveSource()!=null)
			return getArchiveSource().getArchiveObject(caching);
		
		return null;
	}
	
	
	public ArchiveSource getArchiveSource() {

		if (archiveSource==null)
			archiveSource = new ArchiveSource();
		return archiveSource;
	}
	
	public void setUrlObject(URLObject urlObject) {
		this.urlObject = urlObject;
	}
	
	public URLObject getUrlObject() {
		return urlObject;
	}
	
	public void set_ArchiveObject(ArchiveObject _ArchiveObject) {
		this._ArchiveObject = _ArchiveObject;
	}
	
	public ArchiveObject get_ArchiveObject() {
		if (_ArchiveObject==null)
			_ArchiveObject = new ArchiveObject(null);
		return _ArchiveObject;
	}
	
	public String get_currentYear() {
		return _currentYear;
	}
	
	public void set_currentYear(String _currentYear) {
		this._currentYear = _currentYear;
	}
	
	public String get_currentContent() {
		return _currentContent;
	}
	
	public void set_currentContent(String _currentContent) {
		this._currentContent = _currentContent;
	}
	
	public void set_currentURL(String _currentURL) {
		this._currentURL = _currentURL;
	}
	
	public String get_currentURL() {
		return _currentURL;
	}
	
	public HashMap<Utils.CachingSites, String> get_CachedLinksForLink() {
		if (_CachedLinksForLink==null)
			_CachedLinksForLink = new HashMap<Utils.CachingSites,String>();
		return _CachedLinksForLink;
	}
	
	public void set_CachedLinksForLink(
			HashMap<Utils.CachingSites, String> _CachedLinksForLink) {
		this._CachedLinksForLink = _CachedLinksForLink;
	}
	
	public HashMap<Utils.CachingSites, ArrayList<String>> get_ModifiedCachedLinksFromSite() {
		if (_ModifiedCachedLinksFromSite==null)
			_ModifiedCachedLinksFromSite = new HashMap<Utils.CachingSites,ArrayList<String>>();
		return _ModifiedCachedLinksFromSite;
	}
	
	public void set_ModifiedCachedLinksFromSite(HashMap<Utils.CachingSites, ArrayList<String>> _ModifiedCachedLinksFromSite) {
		this._ModifiedCachedLinksFromSite = _ModifiedCachedLinksFromSite;
	}
	
	public HashMap<Utils.CachingSites, ArrayList<String>> get_MultiCacheLinksFromSite() {
		if (_MultiCacheLinksFromSite==null)
			_MultiCacheLinksFromSite = new HashMap<Utils.CachingSites,ArrayList<String>>();
		return _MultiCacheLinksFromSite;
	}
	
	public void set_MultiCacheLinksFromSite(
			HashMap<Utils.CachingSites, ArrayList<String>> _MultiCacheLinksFromSite) {
		this._MultiCacheLinksFromSite = _MultiCacheLinksFromSite;
	}
	
	public ArrayList<String> get_HistoryLinks() {
		if (_HistoryLinks == null)
			_HistoryLinks = new ArrayList<String>();
		return _HistoryLinks;
	}
	
	public void set_HistoryLinks(ArrayList<String> _HistoryLinks) {
		this._HistoryLinks = _HistoryLinks;
	}

	
	public Boolean getCachedLinkResponse(String link, String year, Utils.CachingSites source)
	{
		switch(source)
		{
		
			case WayBackMachine:
			{
				wayback wbParser = new wayback();
				if (wbParser.GetResponse(link,year))
				{
					set_currentURL(wbParser.get_modifiedURL());
					set_currentContent(wbParser.get_Response());
					return true;
				}
			}
			
			case ArchiveIt:
			{
				// Not Supported Yet
			}
		}
			
		return false;
	}
	
	public HashMap<Utils.CachingSites,String> getCacheLinksFromLink(String link, String _Response)
	{
		for(Utils.CachingSites site : Utils.get_SupportedCachingSites())
		{
			switch(site)
			{
			
				case WayBackMachine:
				{
					wayback wbParser = new wayback();

					for (int i=Utils.get_sYear(); i<=Utils.get_eYear(); i++)
					{
						link = wbParser.GetRedirectedLink(link, String.valueOf(i));
						if (link!=null && Utils.CheckIfLinkExists(link,"GET"))
						{
							set_currentURL(link);
							get_CachedLinksForLink().put(Utils.CachingSites.WayBackMachine,link);
						}

					}

				}
				case ArchiveIt:
				{
					// Not Supported Yet
				}
			}
		}
		return get_CachedLinksForLink();
	}

	public HashMap<Utils.CachingSites,ArrayList<String>> getModifiedCacheLinksFromSiteByYear(String _Year)
	{
		ArchiveParams params = null;
		ArchiveObject archiveObject = null;
		
		for(Utils.CachingSites CachingSite : Utils.get_SupportedCachingSites())
		{
			switch(CachingSite)
			{
				case WayBackMachine:
				{
					ArrayList<String> links = new ArrayList<String>();	
					wayback wbParser = new wayback();
					ArrayList<String> _cachedLinks = wbParser.getLinksInPage(this.getUrlObject().get_URL(), _Year);
					ArrayList<String> _links = Utils.ModifyLinksConvention(this.getUrlObject().get_URL(),_cachedLinks);

					if (_links!=null && _links.size() > 0)
					{
						for (String l : _links)
						{
							links.add(l);
						}
					}
						
					if (links!=null)
					{
						
						params = new ArchiveParams();
						params.setLinks(links);
						params.setYear(_Year);
						params.setMessage(wbParser.get_Request());
						params.setUrl(wbParser.get_modifiedURL());
						
						archiveObject = new ArchiveObject(params);
						
						getArchiveSource().addArchiveSource(CachingSite, archiveObject);
						getArchiveSource().getArchiveObject(CachingSite).getArchiveParams().setUrl(wbParser.get_modifiedURL());
						
						get_ModifiedCachedLinksFromSite().put(Utils.CachingSites.WayBackMachine, links);

					}
				}
				break;
			}
		}
		
		return get_ModifiedCachedLinksFromSite();
		
	}
	
	public HashMap<Utils.CachingSites,ArrayList<String>> getModifiedCacheLinksFromSite(String _site)
	{
		ArrayList<String> links = new ArrayList<String>();		
		for(Utils.CachingSites site : Utils.get_SupportedCachingSites())
		{
			switch(site)
			{
				case WayBackMachine:
				{
					wayback wbParser = new wayback();
					for (int i=Utils.get_sYear(); i<=Utils.get_eYear(); i++) {
						ArrayList<String> _links = Utils.ModifyLinksConvention(_site,wbParser.getLinksInPage(_site, String.valueOf(i)));
						for (String l : _links)
							links.add(l);
						
					}
					
					set_currentURL(wbParser.get_modifiedURL());
					get_ModifiedCachedLinksFromSite().put(Utils.CachingSites.WayBackMachine, links);
					return get_ModifiedCachedLinksFromSite();

				}
				case ArchiveIt:
				{
	
				}
			}
		}
		
		return get_ModifiedCachedLinksFromSite();
	}
	
	//Get Links from Parsers
	public HashMap<Utils.CachingSites,ArrayList<String>> getMultiCacheLinksFromSite(String _site, String _Response)
	{
		
		ArrayList<String> sitesArray = new ArrayList<String>();
		GenericParser parser = new GenericParser();
		
		ArrayList<String> SiteLinks = parser.getLinksInPage(_Response);
		if (!SiteLinks.contains((String)_site))
			SiteLinks.add(_site);
		
		ArrayList<String> newSiteLinks = new ArrayList<String>();
		
		for (String link: SiteLinks)
		{
			String newLink = Utils.ModifyLinkConvention(_site,link);
			if (newLink!=null)
				newSiteLinks.add(newLink);
		}
		
		SiteLinks = newSiteLinks;
		
		if (SiteLinks!=null && SiteLinks.size() > 0)
		{
			for (String link : SiteLinks)
			{				
				if (!get_HistoryLinks().contains((String)link)) 
				{
					HashMap<Utils.CachingSites,String> cachedLinks = getCacheLinksFromLink(_site,_Response);
					for (Map.Entry<Utils.CachingSites,String> entry : cachedLinks.entrySet()) 
					{
						
						Utils.CachingSites sourceSite = entry.getKey();
					    String sites = entry.getValue();
					    
							if (link!=null && Utils.CheckIfLinkExists(link,"GET"))
							{
								set_currentURL(link);
								sitesArray.add(link);
								get_MultiCacheLinksFromSite().put(sourceSite,sitesArray);
								get_HistoryLinks().add(link);
							}
							else
							{
								get_HistoryLinks().add(link);
									
							}
					}

				}
			}
		}
		return get_MultiCacheLinksFromSite();
	}
	
	public CacheObject(URLObject url)
	{
		this.setUrlObject(url);
			
	}


}
