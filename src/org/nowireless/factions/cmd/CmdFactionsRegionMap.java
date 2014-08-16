package org.nowireless.factions.cmd;

import org.nowireless.factions.entity.RegionBoardCollections;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsRegionMap extends FCommand {
	public CmdFactionsRegionMap() {
		this.addAliases("map");
		
		this.addOptionalArg("on/off", "once");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_MAP.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform() {
		if(!this.argIsSet(0)) {
			showMap();
			return;
		}
	}
	
	public void showMap() {
		sendMessage(RegionBoardCollections.get().getMap(usenderFaction, PS.valueOf(me), me.getLocation().getYaw()));
	}
}
