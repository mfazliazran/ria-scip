package com.hacktics.vehicle.dejavu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.hacktics.vehicle.dejavu.Utils.CachingSites;

public class ArchiveSource {

	private  HashMap<Utils.CachingSites,ArchiveObject> _ArchiveSource;
	private  ArchiveObject _archiveObject;
	
	public void  addArchiveSource(Utils.CachingSites archiveSource, ArchiveObject archiveObject ) {
		if (archiveSource != null && archiveObject !=null)
			get_ArchiveSource().put(archiveSource, archiveObject);
	}
	
	public ArchiveObject getArchiveObject(Utils.CachingSites archiveSource) {
		if (archiveSource != null)
			return get_ArchiveSource().get(archiveSource);
		
		return null;
	}
	

	public  HashMap<Utils.CachingSites, ArchiveObject> get_ArchiveSource() {
		if (_ArchiveSource == null)
			_ArchiveSource = new HashMap<Utils.CachingSites,ArchiveObject>();
		return _ArchiveSource;
	}
	
	
	public HashMap<Utils.CachingSites,ArrayList<String>> getSourcesLinks(String Year)
	{
		HashMap<Utils.CachingSites,ArrayList<String>> sourcesLinks = new HashMap<Utils.CachingSites,ArrayList<String>>();
		
		if (get_ArchiveSource()!=null)
		{
			for (Entry<CachingSites, ArchiveObject> caching : get_ArchiveSource().entrySet())
			{
				Utils.CachingSites cachingSite = caching.getKey();
				ArchiveObject archiveObject = caching.getValue();
				
				if (archiveObject.getArchiveParams().getYear() == Year)
					sourcesLinks.put(cachingSite, archiveObject.getArchiveParams().getLinks());
			
			}
			return sourcesLinks;
		}
			
		return null;
	}
}
