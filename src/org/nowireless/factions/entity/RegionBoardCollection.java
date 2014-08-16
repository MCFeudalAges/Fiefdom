package org.nowireless.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.nowireless.factions.RegionAccess;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.MUtil;

public class RegionBoardCollection extends Coll<RegionBoard> implements RegionBoardInterface{
	public RegionBoardCollection(String name) {
		super(name, RegionBoard.class, MStore.getDb(), Factions.get(), false, true, true);
	}

	@Override
	public String fixId(Object oid) {
		if (oid == null) return null;
		if (oid instanceof String) return (String)oid;
		if (oid instanceof RegionBoard) return this.getId(oid);

		return MUtil.extract(String.class, "worldName", oid);
	}

	@Override
	public RegionAccess getRegionAccessAt(PS ps) {
		if (ps == null) return null;
		RegionBoard regionMap = this.get(ps.getWorld());
		if (regionMap == null) return null;
		return regionMap.getRegionAccessAt(ps);
	}

	@Override
	public Region getRegionAt(PS ps) {
		if(ps == null) return null;
		RegionBoard regionMap = this.get(ps.getWorld());
		if(regionMap == null) return null;
		return regionMap.getRegionAt(ps);
	}

	public Faction getFactionAt(PS ps) {
		if(ps == null) return null;
		RegionBoard regionBoard = this.get(ps.getWorld());
		if(regionBoard == null) return null;
		return regionBoard.getFactionAt(ps);
	}
	
	@Override
	public void setRegionAccessAt(PS ps, RegionAccess regionAccess) {
		if(ps == null) return;
		RegionBoard regionMap = this.get(ps.getWorld());
		if(regionMap == null) return;
		regionMap.setRegionAccessAt(ps, regionAccess);;
	}

	@Override
	public void setRegionAt(PS ps, Region region) {
		if(ps == null) return;
		RegionBoard regionMap = this.get(ps.getWorld());
		if(regionMap == null) return;
		regionMap.setRegionAt(ps, region);
	}

	@Override
	public void removeAt(PS ps) {
		if(ps == null) return;
		RegionBoard regionMap = this.get(ps.getWorld());
		if(regionMap == null) return;
		regionMap.removeAt(ps);
	}

	@Override
	public void removeAll(Region region) {
		for(RegionBoard regionMap : this.getAll()) {
			regionMap.removeAll(region);
		}
	}

	@Override
	public void clean() {
		for (RegionBoard regionMap : this.getAll())
		{
			regionMap.clean();
		}
	}

	@Override
	public Set<PS> getChunks(Region region) {
		Set<PS> ret = new HashSet<PS>();
		for(RegionBoard regionMap : this.getAll()) {
			ret.addAll(regionMap.getChunks(region));
		}
		return ret;
	}

	@Override
	public int getCount(Region region) {
		int ret = 0;
		for(RegionBoard regionMap : this.getAll()) {
			ret += regionMap.getCount(region);
		}
		return ret;
	}

	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs,
			double inDegrees) {
		if(centerPs == null) return null;
		RegionBoard regionMap = this.get(centerPs.getWorld());
		if(regionMap == null) return null;
		return regionMap.getMap(observer, centerPs, inDegrees);
	}

	@Override
	public Set<Region> getOwnedRegions(Faction faction) {
		Set<Region> ret = new HashSet<Region>();
		for(RegionBoard regionBoard : this.getAll()) {
			ret.addAll(regionBoard.getOwnedRegions(faction));
		}
		return ret;
	}

}
