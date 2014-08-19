package org.nowireless.factions.empire;

import org.bukkit.plugin.Plugin;
import org.nowireless.factions.entity.Region;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.EngineAbstract;

public class EmpireEngine extends EngineAbstract {

	@Override public Plugin getPlugin() { return Factions.get(); }

	
	
	public Result canCreateEmpire(EmpireConf conf, Faction faction) {
		if(!faction.isNone()) {
			if(!faction.isEmpire()) {
				if(faction.getPower() > conf.getMinEmpirePower()) {
					return Result.PROCEED;
				} else {
					return Result.NO_NOT_ENOUGH_POWER;
				}
			} else {
				return Result.NO_IS_EMPIRE;
			}
		} 
		return Result.NO_DEFAULT_FACTION;
	}
	
	public Result canClaim(EmpireConf conf, Faction faction, Region region) {
		
		if(faction.isNone()) {
			return Result.NO_DEFAULT_FACTION;
		}
		
		if(region.isNone() || !region.isClaimable()) {
			return Result.NO_CANT_BE_CLAIMED;
		}
		
		if(region.isOwned()) {
			return Result.NO_ALLREADY_OWNED;
		}
		
		if(region.regionPower() > faction.getPower()) {
			return Result.NO_NOT_ENOUGH_POWER;
		}
		
		return Result.PROCEED;
	}
	
	public Result canInvite(EmpireConf conf, Faction invitor, Faction Invitee) {
		return Result.NO_NOT_IMPLEMENTED_YET;
	}
	
	public Result canLeave(EmpireConf conf, Faction faction) {
		return Result.NO_NOT_IMPLEMENTED_YET;
	}
	
	public Result canJoin(EmpireConf conf, Faction empire, Faction joinee) {
		return Result.NO_NOT_IMPLEMENTED_YET;
	}
	
	public Result canRelation(EmpireConf conf, Faction me, Faction you, Relations relation) {
		return Result.NO_NOT_IMPLEMENTED_YET;
	}
	
	public Result canRankChange(EmpireConf conf, Faction faction, Rank rank) {
		return Result.NO_NOT_IMPLEMENTED_YET;
	}
}
