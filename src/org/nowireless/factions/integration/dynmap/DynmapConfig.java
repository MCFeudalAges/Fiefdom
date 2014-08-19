package org.nowireless.factions.integration.dynmap;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.SimpleConfig;

public class DynmapConfig extends SimpleConfig {
	
	private static transient DynmapConfig i = new DynmapConfig();
	public static DynmapConfig get() { return i; }
	public DynmapConfig() { super(Factions.get(), "dynmap"); }

	
	public String regionLayerName = "Regions";
	public int minZoom = 0;
	public int layerPriority = 10;
	public boolean layerHidenByDefault = false;
	public boolean use3d = false;
	public long updatePeriodMS = 300;
	
}
