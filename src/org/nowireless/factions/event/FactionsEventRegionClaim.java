package org.nowireless.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.nowireless.factions.entity.Region;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsAbstractSender;

public class FactionsEventRegionClaim extends EventFactionsAbstractSender{
	
	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }

	private final Faction oldFaction;
	public Faction getOldFaction() { return oldFaction; }

	private final String oldFactionID;
	public String getOldFactionID() { return oldFactionID; }

	private final Faction newFaction;
	public Faction getNewFaction() { return newFaction; }

	private final String newFactionID;
	public String getNewFactionID() { return newFactionID; }

	private final Region region;
	public Region getRegion() { return region; }

	public FactionsEventRegionClaim(CommandSender sender, Region region, Faction oldFaction, Faction newFaction) {
		super(sender);
		// TODO Auto-generated constructor stub
		this.region = region;
		this.oldFaction = oldFaction;
		this.oldFactionID = oldFaction.getId();
		this.newFaction = newFaction;
		this.newFactionID = newFaction.getId();
	}
	
	public String getType() {
		//TODO Issue #14
		return "Claim";
	}

}
