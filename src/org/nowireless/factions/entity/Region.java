package org.nowireless.factions.entity;


import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;

public class Region extends Entity<Region>{
	public static Region get(Object oid) {
		return RegionCollections.get().get2(oid);
	}

	///////////////////////////////////////////////////////////////////////

	@Override
	public Region load(Region that) {
		this.setName(that.name);
		this.setOwnerFaction(that.ownerFactionId);
		this.setClaimable(that.claimable);
		this.setCreatedAtMillis(that.createdAtMillis);
		//this.setWorld(that.world);
		this.setOwnerFactionName(that.factionName);

		return this;
	}

	@Override
	public void preDetach(String id) {
		String universe = this.getUniverse();

		// Clean the board
		BoardColls.get().getForUniverse(universe).clean();

		// Clean the uplayers
		UPlayerColls.get().getForUniverse(universe).clean();

		RegionBoardCollections.get().getForUniverse(universe).clean();
	}

	////////////////////////////////////////////////////////////
	/*
	 * Raw values
	 */
	private String name = null;

	private String ownerFactionId = null;

	private long createdAtMillis = System.currentTimeMillis();

	//Todo Change to boolean at some point
	private Boolean claimable = false;

	private String world = null;

	private String factionName = null;

	//private List

	////////////////////////////////////////////////////////////

	public boolean isNone() {
		return this.getId().equals(UConf.get(this).regionIdNone);
	}

	public boolean isNormal()
	{
		return ! this.isNone();
	}

	////////////////////////////////////////////////////////////

	@Deprecated
	public String getOwnerFaction() {
		return this.ownerFactionId;
	}

	public String getOwningFactionId() {
		if(ownerFactionId == null) return UConf.get(this).factionIdNone;
		return this.ownerFactionId;
	}
	
	public boolean isOwned() {
		if(this.getOwningFactionId().equalsIgnoreCase(UConf.get(this).factionIdNone)) return false;
		return this.ownerFactionId != null;
	}
	
	public Faction getOwningFaction() {
		return FactionColls.get().get(this).get(this.getOwningFactionId());
	}
	
	public String getFactionName() {
		return this.getOwningFaction().getName();
	}

	public void setOwnerFaction(String owner) {
		String target = owner;
		if(target == null) return;
		if(MUtil.equals(this.ownerFactionId, target)) return;
		this.ownerFactionId = target;
		this.changed();
	}

	@Deprecated
	public void setOwnerFactionName(String factName) {
		String name = factName;
		if(name == null) return;
		if(MUtil.equals(this.factionName, name)) return;
		this.factionName = name;
		this.changed();
	}
	
	
	public void setOwnerFaction(Faction faction) {
		String id = faction.getId();
		this.setOwnerFaction(id);
	}
	////////////////////////////////////////////////////////////

	@Deprecated
	public String getWorld() {
		return this.world;
	}
	
	@Deprecated
	public void setWorld(String newWorld) {
		if((this.world != null) && (this.world.equals(newWorld))) return;
		this.world = newWorld;
		this.changed();
	}
	public boolean isClaimable() {
		return this.claimable;
	}

	public void setClaimable(boolean claim) { 
		if(this.claimable == claim) return;
		this.claimable = claim;
		this.changed();
	}
	////////////////////////////////////////////////////////////

	public String getName() {
		String ret = this.name;
		//ret = ret.toUpperCase();
		return ret;
	}

	public void setName(String name) {
		String target = name;
		if(MUtil.equals(this.name, target)) return;
		this.name = target;
		this.changed();
	}

	///////////////////////////////////////////////////////////////////////

	public String getComparisonName()
	{
		return MiscUtil.getComparisonString(this.getName());
	}

	public String getName(String prefix)
	{
		return prefix + this.getName();
	}

	/*
	 * public String getName(RelationParticipator observer) {
	 * 	if (observer == null) return getName();
	 *	return this.getName(this.getColorTo(observer).toString());
	*}
	*/

	////////////////////////////////////////////////////////////////////////////////
	public long getCreatedAtMillis()
	{
		return this.createdAtMillis;
	}

	public void setCreatedAtMillis(long createdAtMillis)
	{
		// Clean input
		long target = createdAtMillis;

		// Detect Nochange
		if (MUtil.equals(this.createdAtMillis, createdAtMillis)) return;

		// Apply
		this.createdAtMillis = target;

		// Mark as changed
		this.changed();
	}


}
