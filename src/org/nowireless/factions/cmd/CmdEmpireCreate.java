package org.nowireless.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.util.Txt;

public class CmdEmpireCreate extends FCommand {
	public CmdEmpireCreate() {
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.EMPIRE_CREATE.node));
		this.addRequirements(ReqHasFaction.get());
	}
	
	@Override
	public void perform() {
		if(!Rel.LEADER.equals(usender.getRole())) {
			this.sendMessage(Txt.parse("<bad>Only Leaders can create a Empire from a faction"));
			return;
		}
		
		if(usenderFaction.isEmpire()) {
			this.sendMessage(Txt.parse("<bad>You are already a empire"));
			return;
		}
		
		if(UConf.get(usender).minEmpirePower > usenderFaction.getPower()) {
			List<String> msg = new ArrayList<String>();
			msg.add(Txt.parse("<bad>Your faction does not have enough power to create a empire"));
			this.sendMessage(msg);
			return;
		}
		
		//TODO run event
		usenderFaction.setEmpire(true);
		
		usenderFaction.sendMessage(Txt.parse("<info>You faction is now a empire"));
	}

}
