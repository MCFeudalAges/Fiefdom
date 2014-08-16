package org.nowireless.factions.cmd;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsShowRegion extends FCommand {
	public CmdFactionsShowRegion() {
		this.addAliases("show");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.SHOW_REGION.node));
	}
	
	@Override
	public void perform() {
		PS chunk = PS.valueOf(me).getChunk(true);
		Region region = RegionBoardCollections.get().getRegionAt(chunk);
		Faction owner = region.getOwningFaction();
		
		msg("Region - " + region.getName() + " Owner - " + owner.getName());
	}
}
