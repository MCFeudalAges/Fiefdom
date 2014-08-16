package org.nowireless.factions.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.ChatColor;
import org.nowireless.factions.RegionAccess;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

public class RegionBoard extends Entity<RegionBoard> implements RegionBoardInterface{

	public static final transient Type MAP_TYPE = new TypeToken<Map<PS, RegionAccess>>(){}.getType();

	public static RegionBoard get(Object oid) {
		return RegionBoardCollections.get().get2(oid);
	}

	@Override
	public RegionBoard load(RegionBoard that) {
		this.map = that.map;
		return this;
	}

	private ConcurrentSkipListMap<PS, RegionAccess> map;
	public Map<PS, RegionAccess> getMap() { return Collections.unmodifiableMap(this.map); }

	public RegionBoard() {
		this.map = new ConcurrentSkipListMap<PS, RegionAccess>();
	}

	public RegionBoard(Map<PS, RegionAccess> map) {
		this.map = new ConcurrentSkipListMap<PS, RegionAccess>(map);
	}

	@Override
	public RegionAccess getRegionAccessAt(PS ps) {
		if(ps == null) return null;
		ps = ps.getChunkCoords(true);
		RegionAccess ret = this.map.get(ps);
		if(ret == null) ret = RegionAccess.valueOf(UConf.get(this).regionIdNone);
		return ret;
	}

	@Override
	public Region getRegionAt(PS ps) {
			if(ps == null) return null;
			RegionAccess ra = this.getRegionAccessAt(ps);
			if(ra == null) return null;
			return ra.getAssociatedRegion(this);
	}
	
	public Faction getFactionAt(PS ps) {
		if(ps == null) return null;
		RegionAccess ra = this.getRegionAccessAt(ps);
		if(ra == null) return null;
		return ra.getAssociatedRegion(this).getOwningFaction();
	}

	@Override
	public void setRegionAccessAt(PS ps, RegionAccess regionAccess) {
		ps = ps.getChunkCoords(true);

		if(regionAccess == null || (regionAccess.getAssociatedRegionID().equals(UConf.get(this).regionIdNone))) {
			this.map.remove(ps);
		} else {
			this.map.put(ps, regionAccess);
		}
		this.changed();
	}

	@Override
	public void setRegionAt(PS ps, Region region) {
		RegionAccess regionAccess = null;
		if(region != null) {
			regionAccess = RegionAccess.valueOf(region.getId());
		}
		this.setRegionAccessAt(ps, regionAccess);
	}

	@Override
	public void removeAt(PS ps) {
		this.setRegionAccessAt(ps, null);
	}

	@Override
	public void removeAll(Region region) {
		String regionId = region.getId();
		for(Entry<PS, RegionAccess> entry : this.map.entrySet()) {
			RegionAccess regionAccess = entry.getValue();
			if(!regionAccess.getAssociatedRegionID().equals(regionId)) continue;

			PS ps = entry.getKey();
			this.removeAt(ps);
		}
	}

	@Override
	public Set<PS> getChunks(Region region) {
		return this.getChunks(region.getId());
	}

	public  Set<PS> getChunks(String regionID) {
		Set<PS> ret = new HashSet<PS>();
		for (Entry<PS, RegionAccess> entry : this.map.entrySet()) {
			RegionAccess ra = entry.getValue();
			if(!ra.getAssociatedRegionID().equals(regionID)) continue;

			PS ps = entry.getKey();
			ps = ps.withWorld(this.getId());
			ret.add(ps);
		}
		return ret;
	}

	@Override
	public int getCount(Region region) {
		return this.getCount(region.getId());
	}

	public int getCount(String regionID) {
		int ret = 0;
		for(RegionAccess ra : this.map.values()) {
			if(!ra.getAssociatedRegionID().equals(regionID)) continue;
			ret++;
		}
		return ret;
	}
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees) {
		centerPs = centerPs.getChunkCoords(true);
		ArrayList<String> ret = new ArrayList<String>();
		Region centerRegion = this.getRegionAt(centerPs);
		
		ret.add(Txt.titleize("("+centerPs.getChunkX() + "," + centerPs.getChunkZ()+") "+centerRegion.getName()));
		
		int halfWidth = Const.MAP_WIDTH / 2;
		int halfHeight = Const.MAP_HEIGHT / 2;
		
		PS topLeftPs = centerPs.plusChunkCoords(-halfWidth, -halfHeight);
		
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;
		
		// Make room for the list of names
		height--;
		
		Map<Region, Character> rList = new HashMap<Region, Character>();
		int chrIdx = 0;

		// For each row
		for (int dz = 0; dz < height; dz++) {
			// Draw and add that row
			String row = "";
			for (int dx = 0; dx < width; dx++) {
				if (dx == halfWidth && dz == halfHeight) {
					row += ChatColor.AQUA + "+";
					continue;
				}

				PS herePs = topLeftPs.plusChunkCoords(dx, dz);
				Region hereFaction = this.getRegionAt(herePs);
				if (hereFaction.isNone()) {
					row += ChatColor.GRAY + "-";
				} else {
					if (!rList.containsKey(hereFaction))
						rList.put(hereFaction, Const.MAP_KEY_CHARS[chrIdx++]);
					char rchar = rList.get(hereFaction);
					row += MConf.get().colorNeutral + "" + rchar;
				}
			}
			ret.add(row);
		}
		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, Txt.parse("<a>"));

		// Add the compass
		ret.set(1, asciiCompass.get(0) + ret.get(1).substring(3 * 3));
		ret.set(2, asciiCompass.get(1) + ret.get(2).substring(3 * 3));
		ret.set(3, asciiCompass.get(2) + ret.get(3).substring(3 * 3));
		
		String fRow = "";
		for (Region keyfaction : rList.keySet())
		{
			fRow += ""+ MConf.get().colorNeutral + rList.get(keyfaction) + ": " + keyfaction.getName() + " ";
		}
		fRow = fRow.trim();
		ret.add(fRow);
		
		return ret;
	}

	@Override
	public void clean() {
		//RegionBoardCollection regionMapColl = RegionBoardCollections.get().get(this);
		FactionColl factionColl = FactionColls.get().get(this);
		RegionCollection regionColl = RegionCollections.get().get(this);
		
		for(Entry<PS, RegionAccess> entry : this.map.entrySet()) {
			RegionAccess regionAccess = entry.getValue();
			String  regionID = regionAccess.getAssociatedRegionID();
			
			
			boolean goodRegionId = regionColl.containsId(regionID);
			if(!goodRegionId) {
				PS ps = entry.getKey();
				this.removeAt(ps);

				Factions.get().log("RegionMap Cleaner Removed " + regionID + " from " + ps);
			} else {
				String ownerFactionID = regionAccess.getOwningFactionId(this);
				boolean goodFactionId = factionColl.containsId(ownerFactionID);
				
				if(goodFactionId) {
					Factions.get().log(Txt.parse("<good>Region ID <info>"+ regionID + " <good> with owner Faction ID <info>" + ownerFactionID + "<good> is good" ));
					continue;
				} else {
					Region region = regionColl.get(regionID);
					region.setOwnerFaction(UConf.get(this).factionIdNone);
					Factions.get().log("Set region " + regionID + "owner to " + UConf.get(this).factionIdNone);
				}
			}
		}
	}

	@Override
	public Set<Region> getOwnedRegions(Faction faction) {
		String factionID = faction.getId();
		
		Set<Region> ret = new HashSet<Region>();
		for (Entry<PS, RegionAccess> entry : this.map.entrySet()) {
			RegionAccess ra = entry.getValue();
			
			Region region = ra.getAssociatedRegion(faction);
			if(ret.contains(region)) continue;
			
			String owner = region.getOwningFactionId();
			if(MUtil.equals(factionID, owner)) continue;
			
			ret.add(region);
		}
		return ret;
	}
}
