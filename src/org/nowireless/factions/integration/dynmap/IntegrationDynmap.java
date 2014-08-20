package org.nowireless.factions.integration.dynmap;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationDynmap extends IntegrationAbstract {
	private static IntegrationDynmap i = new IntegrationDynmap();
	public static IntegrationDynmap get() { return i; }
	private IntegrationDynmap() { super("dynmap"); }
	
	@Override
	public void activate() {
		EngineDynmap.get().activate();
	}
	
	@Override
	public void deactivate() {
		EngineDynmap.get().deactivate();
	}
}
