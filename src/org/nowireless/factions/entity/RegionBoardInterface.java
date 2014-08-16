package org.nowireless.factions.entity;

import java.util.ArrayList;
import java.util.Set;

import org.nowireless.factions.RegionAccess;

import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

public interface RegionBoardInterface {
	public RegionAccess getRegionAccessAt(PS ps);
	public Region getRegionAt(PS ps);
	
	public Faction getFactionAt(PS ps);

	public void setRegionAccessAt(PS ps, RegionAccess regionAccess);
	public void setRegionAt(PS ps, Region region);

	public void removeAt(PS ps);
	public void removeAll(Region region);
	public void clean();

	public Set<PS> getChunks(Region region);

	public int getCount(Region region);
	
	public Set<Region> getOwnedRegions(Faction faction);
	
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees);
}
