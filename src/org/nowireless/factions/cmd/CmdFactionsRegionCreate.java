package org.nowireless.factions.cmd;

import java.util.ArrayList;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionCollection;
import org.nowireless.factions.entity.RegionCollections;
import org.nowireless.factions.event.FactionsEventRegionCreate;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.store.MStore;

public class CmdFactionsRegionCreate extends FCommand{
	public CmdFactionsRegionCreate() {
		this.addAliases("create", "c");
		this.addRequiredArg("name");
		//this.addRequiredArg("world");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.CREATE_REGION.node));
	}
	
	@Override
	public void perform() {
		String newName = this.arg(0);
		//String newWorld = this.arg(1);
		
		Factions.get().log("Retriving Region Collection");
		RegionCollection coll = RegionCollections.get().get(usender);
		
		if(coll.isNameTaken(newName)) {
			msg("<b>That name is already in use.");
			return;
		}
		
		ArrayList<String> nameValidationErrors = coll.validateName(newName);
		if (nameValidationErrors.size() > 0) {
			sendMessage(nameValidationErrors);
			return;
		}
		
		String regionId = MStore.createId();
		
		FactionsEventRegionCreate event = new FactionsEventRegionCreate(sender, coll.getUniverse(), regionId, newName);
		event.run();
		if(event.isCancelled()) return;
		
		Region region = coll.create(regionId);
		region.setName(newName);
		//region.setWorld(newWorld);
		
		String msg = "Created Region: " + region.getName() + "with ID:" + region.getId();
		usender.sendMessage(msg);
		Factions.get().log(msg);
	}
}
