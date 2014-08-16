package org.nowireless.factions.cmd;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;
import org.nowireless.factions.event.FactionsEventRegionClaim;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsRegionUnclaim extends FCommand {
	public CmdFactionsRegionUnclaim() {
		this.addAliases("unclaim");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_UNCLAIM.node));
	}
	
	@Override
	public void perform() {
		PS chunk = PS.valueOf(me).getChunk(true);
		Region region = RegionBoardCollections.get().getRegionAt(chunk);
		
		if(region.isNone()) {
			msg("No region to unclaim");
			return;
		}
		
		if(!region.getOwningFactionId().equalsIgnoreCase(usenderFaction.getId())) {
			msg("Your faction does not own this region");
			return;
		}
		
		if(!region.isClaimable()) {
			msg("Region can't be unclaimed");
			return;
		}
		
		FactionsEventRegionClaim event = new FactionsEventRegionClaim(sender, region, region.getOwningFaction(), usenderFaction);
		event.run();
		if(event.isCancelled()) return;
		
		region.setOwnerFaction(FactionColls.get().get(usender).getNone());
		
		msg(region.getName() + " is now unclaimed");
	}
}
