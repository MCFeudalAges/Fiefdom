package org.nowireless.factions.cmd;

import org.nowireless.factions.cmd.arg.ARRegion;
import org.nowireless.factions.entity.Region;
import org.nowireless.factions.event.FactionsEventRegionDestroy;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;


public class CmdFactionsRegionDissolve extends FCommand {
	public CmdFactionsRegionDissolve() {
		this.addAliases("disolve", "d");
		this.addRequiredArg("region");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_DISOLVE.node));
	}
	
	@Override
	public void perform() {
		Region region = this.arg(0, ARRegion.get(usender));
		if(region == null) return;
		
		if(region.getId() == UConf.get(sender).regionIdNone) return;
		
		FactionsEventRegionDestroy event = new FactionsEventRegionDestroy(sender, region.getUniverse(), region.getId(), region.getName());
		event.run();
		if(event.isCancelled()) return;
		
		Factions.get().log(Txt.parse("<i>The Region <h>%s <i>(<h>%s<i>) was disolved by <h>%s<i>.", region.getName(), region.getId(), usender.getName()));
		usender.msg("<i>The Region <h>%s <i>(<h>%s<i>) has been disolved!",region.getName(), region.getId());

		region.detach();
	}
}
