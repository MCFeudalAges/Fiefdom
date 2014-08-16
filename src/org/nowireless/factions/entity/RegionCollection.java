package org.nowireless.factions.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.Txt;

import org.bukkit.ChatColor;

public class RegionCollection extends Coll<Region>{
	public RegionCollection(String name) {
		super(name, Region.class, MStore.getDb(), Factions.get());
	}

	@Override
	public void init() {
		super.init();
		this.createSpecialRegions();
	}

	@Override
	public Region get(Object oid) {
		Region ret = super.get(oid);

		if(ret == null && Factions.get().isDatabaseInitialized()) {
			String msg = Txt.parse("<b>Non existing regionID <h>%s requested. <i>Cleaning all boards and uplayers", this.fixId(oid));
			Factions.get().log(msg);

			//BoardColls.get().clean();
			UPlayerColls.get().clean();
			RegionBoardCollections.get().clean();
		}
		return ret;
	}

	public void createSpecialRegions() {
		this.getNone();
	}

	public ArrayList<String> validateName(String str) {
		ArrayList<String> errors = new ArrayList<String>();

		//TODO Make 3 a const
		if(MiscUtil.getComparisonString(str).length() < 3) {
			errors.add(Txt.parse("<i>The region name can't be shorter that <h>%s<i> chars", 3));
		}
		//TODO Make 16 a const
		if(str.length() > 16) {
			errors.add(Txt.parse("<i>The region name can't be longer that <h>%s<i> chars", 16));
		}

		for(char c : str.toCharArray()) {
			if(!MiscUtil.substanceChars.contains(String.valueOf(c))) {
				errors.add(Txt.parse("<i>Region name must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
			}
		}
		return errors;
	}

	public Region getByName(String str) {
		String compStr = MiscUtil.getComparisonString(str);
		for (Region region : this.getAll())
		{
			if (region.getComparisonName().equals(compStr))
			{
				return region;
			}
		}
		return null;
	}

	public Region getBestNameMatch(String searchFor)
	{
		Map<String, Region> name2region = new HashMap<String, Region>();

		// TODO: Slow index building
		for (Region region : this.getAll())
		{
			name2region.put(ChatColor.stripColor(region.getName()), region);
		}

		String tag = Txt.getBestCIStart(name2region.keySet(), searchFor);
		if (tag == null) return null;
		return name2region.get(tag);
	}

	public boolean isNameTaken(String str)
	{
		return this.getByName(str) != null;
	}
	public Region getNone() {
		String id = UConf.get(this).regionIdNone;
		Region region = this.get(id);
		if(region != null) return region;

		region = this.create(id);
		region.setOwnerFaction(UConf.get(this).factionIdNone);
		region.setName("Wilderness");

		return region;
	}

}
