package org.nowireless.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.nowireless.factions.RegionAccess;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.XColls;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.ps.PS;

public class RegionBoardCollections  extends XColls<RegionBoardCollection, RegionBoard> implements RegionBoardInterface {
	private static RegionBoardCollections i = new RegionBoardCollections();
	public static RegionBoardCollections get() { return i; }

	@Override
	public RegionBoardCollection createColl(String collName) {
		return new RegionBoardCollection(collName)
;	}

	@Override
	public Aspect getAspect() {
		return Factions.get().getAspect();
	}

	@Override
	public String getBasename() {
		return Const.COLLECTION_REGION_BOARD;
	}

	@Override
	public void init() {
		super.init();
	}
	@Override
	public RegionAccess getRegionAccessAt(PS ps) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return null;
		return coll.getRegionAccessAt(ps);
	}
	@Override
	public Region getRegionAt(PS ps) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return null;
		return coll.getRegionAt(ps);
	}
	
	public Faction getFactionAt(PS ps) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return null;
		return coll.getFactionAt(ps);
	}
	@Override
	public void setRegionAccessAt(PS ps, RegionAccess regionAccess) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return;
		coll.setRegionAccessAt(ps, regionAccess);
	}
	@Override
	public void setRegionAt(PS ps, Region region) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return;
		coll.setRegionAt(ps, region);
	}
	@Override
	public void removeAt(PS ps) {
		RegionBoardCollection coll = this.getForWorld(ps.getWorld());
		if(coll == null) return;
		coll.removeAt(ps);
	}
	@Override
	public void removeAll(Region region) {
		for(RegionBoardCollection coll : this.getColls()) {
			coll.clean();
		}

	}
	@Override
	public Set<PS> getChunks(Region region) {
		Set<PS> ret = new HashSet<PS>();
		for(RegionBoardCollection coll: this.getColls()) {
			ret.addAll(coll.getChunks(region));
		}
		return ret;
	}
	
	@Override
	public int getCount(Region region) {
		int ret = 0;
		for(RegionBoardCollection coll : this.getColls()) {
			ret += coll.getCount(region);
		}
		return ret;
	}
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs,
			double inDegrees) {
		RegionBoardCollection coll = this.getForWorld(centerPs.getWorld());
		if(coll == null) return null;
		return coll.getMap(observer, centerPs, inDegrees);
	}

	@Override
	public void clean() {
		for(RegionBoardCollection coll : this.getColls()) {
			coll.clean();
		}
	}
	
	@Override
	public Set<Region> getOwnedRegions(Faction faction) {
		Set<Region> ret = new HashSet<Region>();
		for(RegionBoardCollection coll: this.getColls()) {
			ret.addAll(coll.getOwnedRegions(faction));
		}
		return ret;
	}
}
