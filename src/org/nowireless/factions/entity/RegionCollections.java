package org.nowireless.factions.entity;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.XColls;
import com.massivecraft.massivecore.Aspect;

public class RegionCollections  extends XColls<RegionCollection, Region>{
	public static RegionCollections i = new RegionCollections();
	public static RegionCollections get() { return i; }

	@Override
	public RegionCollection createColl(String collName) {
		return new RegionCollection(collName);
	}

	@Override
	public Aspect getAspect() {
		return Factions.get().getAspect();
	}

	@Override 
	public String getBasename() {
		return Const.COLLECTION_REGION;
	}

	@Override
	public void init() {
		super.init();
	}
}
