package org.nowireless.factions.cmd;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;
import org.nowireless.factions.event.FactionsEventRegionClaim;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsRegionClaim extends FCommand {
	public CmdFactionsRegionClaim() {
		this.addAliases("claim");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_CLAIM.node));
	}
	
	@Override
	public void perform() {
		PS chunk = PS.valueOf(me).getChunk(true);
		Region region = RegionBoardCollections.get().getRegionAt(chunk);
		
		if(region.isNone()) {
			msg("Cant claim the " + region.getName() + " region");
			return;
		}
		
		if(!region.isClaimable()) {
			msg("Region can't be claimed");
			return;
		}
		
		FactionsEventRegionClaim event = new FactionsEventRegionClaim(sender, region, region.getOwningFaction(), usenderFaction);
		event.run();
		if(event.isCancelled()) return;
		region.setOwnerFaction(usenderFaction);
		
		msg(usenderFaction.getName() + " now owns " + region.getName());
	}
}
