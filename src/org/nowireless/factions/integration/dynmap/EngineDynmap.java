package org.nowireless.factions.integration.dynmap;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Factions;

public class EngineDynmap implements Listener{
	private static EngineDynmap i = new EngineDynmap();
	public static EngineDynmap get() { return i; }
	private EngineDynmap() {}
	
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate() {
		HandlerList.unregisterAll(this);
	}
}
