package org.nowireless.factions.cmd;

import org.nowireless.factions.RegionAccess;
import org.nowireless.factions.cmd.arg.ARRegion;
import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;
import org.nowireless.factions.event.FactionsEventRegionChunkChange;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRegionAddChunk extends FCommand {
	public CmdFactionsRegionAddChunk() {
		this.addAliases("addchunk", "ac");
		this.addRequiredArg("region");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION_ADD_CHUNK.node));
	}
	
	@Override
	public void perform() {
		final Region forRegion = this.arg(0, ARRegion.get(usender));
		if(forRegion == null) return;
		this.addChunk(forRegion, PS.valueOf(me));
	}

	private void addChunk(Region newRegion, PS ps) {
		if(!usender.isUsingAdminMode()) {
			msg("Must be using admin mode");
			return;
		}
		
		PS chunk = ps.getChunk(true);
		Region oldRegion = RegionBoardCollections.get().getRegionAt(chunk);

		if(newRegion == oldRegion) {
			msg(Txt.parse("Chunk is already a part of the region <value>" + newRegion.getName()));
			return;
		}

		RegionAccess ra = RegionBoardCollections.get().getRegionAccessAt(chunk);
		if(ra != null) {
			if(!ra.getAssociatedRegionID().equals(UConf.get(me).regionIdNone)) {
				msg("Chunk is alreadly apart of another region");
				return;
			}
		}

		//if(!chunk.getWorld().equals(newRegion.getWorld())) {
		//	msg("This chunk is not as the same world as the region");
		//	return;
		//}

		

		FactionsEventRegionChunkChange event = new FactionsEventRegionChunkChange(sender, chunk, oldRegion.getName(), oldRegion.getId(), newRegion.getName(), newRegion.getId());
		event.run();
		if(event.isCancelled()) return;

		RegionBoardCollections.get().setRegionAt(chunk, newRegion);

		String chunkCoords = "[" + chunk.getChunkX() + "," + chunk.getChunkZ() + "," + chunk.getWorld() + "]";
		String msg = "Added Chunk: " + chunkCoords + " to region: " + newRegion.getName();
		usender.sendMessage(msg);
		Factions.get().log(msg);
	}
}
