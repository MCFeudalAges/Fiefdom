package org.nowireless.factions.cmd;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;
import org.nowireless.factions.event.FactionsEventRegionChanged;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsRegionClaimable extends FCommand {
	public CmdFactionsRegionClaimable() {
		this.addAliases("setClaimable");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_SET_CLAIMABLE.node));
	}
	
	@Override
	public void perform() { 
		if(!usender.isUsingAdminMode()) {
			msg("Must be using admin mode");
			return;
		}
		
		PS chunk = PS.valueOf(me);
		Region region = RegionBoardCollections.get().getRegionAt(chunk);
		
		if(region.isNone()) {
			msg("The " + region.getName() + " can't be made claimable");
			return;
		}
		
		if(region.isClaimable()) {
			msg("Region is already claimable");
			return;
		}
		
		FactionsEventRegionChanged event = new FactionsEventRegionChanged(sender, region);
		event.run();
		if(event.isCancelled()) return;
		
		Factions.get().log("Region:" + region.getName() + " ID: " + region.getId() +" is now claimable");
		msg("Claiming is now enabled for " + region.getName());
		region.setClaimable(true);
	}
}
