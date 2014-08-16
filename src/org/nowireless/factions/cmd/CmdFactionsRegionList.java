package org.nowireless.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionCollections;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRegionList extends FCommand {
	public static final int kPageHeight = 9;
	
	public CmdFactionsRegionList() {
		this.addAliases("list");
		this.addOptionalArg("page", "1");
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_LIST.node));
	}
	
	@Override
	public void perform() {
		Integer pageWanted = this.arg(0, ARInteger.get(), 1);
		List<String> lines = new ArrayList<String>();
		ArrayList<Region> regions = new ArrayList<Region>(RegionCollections.get().get(sender).getAll());
		
		int pageCont = (regions.size() / kPageHeight) + 1;
		if(pageWanted > pageCont) {
			pageWanted = pageCont;
		} else if(pageWanted < 1) {
			pageWanted = 1;
		}
		
		int start = (pageWanted - 1) * kPageHeight;
		int end = start + kPageHeight;
		if(end > regions.size()) {
			end = regions.size();
		}
		
		lines.add(Txt.titleize("Region list " + pageWanted + "/" + pageCont));
		for (Region region : regions.subList(start, end)) {
			lines.add(Txt.parse("<i>%s<white> owned by <aqua>%s<aqua>", region.getName(), region.getOwningFaction().getName()));
		}
		sendMessage(lines);
	}
}
