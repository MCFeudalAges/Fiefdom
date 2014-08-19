package org.nowireless.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.nowireless.factions.entity.Region;

import com.massivecraft.factions.event.EventFactionsAbstractSender;

/*
 * This event is for changes to regions that are not covered in other region events
 */
public class FactionsEventRegionChanged extends EventFactionsAbstractSender {

	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	public FactionsEventRegionChanged(CommandSender sender, Region region) {
		super(sender);
		this.region = region;
	}
	
	private final Region region;
	public final Region getRegion() { return region; }

}
