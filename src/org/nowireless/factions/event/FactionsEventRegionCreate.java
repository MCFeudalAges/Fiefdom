package org.nowireless.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.event.EventFactionsAbstractSender;

public class FactionsEventRegionCreate extends EventFactionsAbstractSender {
private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	private final String universe;
	public final String getUniverse() { return this.universe; }
	
	private final String regionId;
	public  final String getREgionId() { return this.regionId; }
	
	private final String regionName;
	public final String getRegionName() { return this.regionName; }

	public FactionsEventRegionCreate(CommandSender sender, String universe, String regionId, String regionName) {
		super(sender);
		this.universe = universe;
		this.regionId = regionId;
		this.regionName = regionName;
	}
}
