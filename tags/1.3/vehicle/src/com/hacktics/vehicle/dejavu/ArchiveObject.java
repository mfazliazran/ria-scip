package com.hacktics.vehicle.dejavu;

import java.util.HashMap;

public class ArchiveObject {

	private HashMap<Integer,ArchiveParams> ArchiveObject;
	private ArchiveParams archiveParams;
	

	public void setArchiveParams(ArchiveParams archiveParams) {
		this.archiveParams = archiveParams;
	}
	
	public ArchiveParams getArchiveParams() {
		return archiveParams;
	}
	
	public ArchiveObject(ArchiveParams params)
	{
		setArchiveParams(params);
	}

	private HashMap<Integer, ArchiveParams> getArchiveObject() {
		if (ArchiveObject == null)
		{
			archiveParams = new ArchiveParams();
			ArchiveObject = new HashMap<Integer,ArchiveParams>();
		}
		return ArchiveObject;
	}
	
}
