package org.nowireless.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsRegion extends FCommand {
	public CmdFactionsRegionCreate cmdFactionsRegionCreate = new CmdFactionsRegionCreate();
	public CmdFactionsRegionDissolve cmdFactionsRegionDissolve = new CmdFactionsRegionDissolve();
	public CmdFactionsRegionAddChunk cmdFactionsRegionAddChunk = new CmdFactionsRegionAddChunk();
	public CmdFactionsRegionRemoveChunk cmdFactionsRegionRemoveChunk = new CmdFactionsRegionRemoveChunk();
	public CmdFactionsRegionClaim cmdFactionsRegionClaim = new CmdFactionsRegionClaim();
	public CmdFactionsRegionUnclaim cmdFactionsRegionUnclaim = new CmdFactionsRegionUnclaim();
	public CmdFactionsShowRegion cmdFactionsShowRegion = new CmdFactionsShowRegion();
	public CmdFactionsRegionClaimable cmdFactionsRegionClaimable = new CmdFactionsRegionClaimable();
	public CmdFactionsRegionList cmdFactionsRegionList = new CmdFactionsRegionList();
	public CmdFactionsRegionMap cmdFactionsRegionMap = new CmdFactionsRegionMap();
	
	public CmdFactionsRegion() {
		this.addSubCommand(this.cmdFactionsRegionCreate);
		this.addSubCommand(this.cmdFactionsRegionDissolve);
		this.addSubCommand(this.cmdFactionsRegionAddChunk);
		this.addSubCommand(this.cmdFactionsRegionRemoveChunk);
		this.addSubCommand(this.cmdFactionsRegionClaim);
		this.addSubCommand(this.cmdFactionsRegionUnclaim);
		this.addSubCommand(this.cmdFactionsShowRegion);
		this.addSubCommand(this.cmdFactionsRegionClaimable);
		this.addSubCommand(this.cmdFactionsRegionList);
		this.addSubCommand(this.cmdFactionsRegionMap);
		
		this.addAliases("region");
		
		this.addRequirements(ReqFactionsEnabled.get());
		//this.addRequirements(ReqBankCommandsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.REGION.node));
	}
	
}
