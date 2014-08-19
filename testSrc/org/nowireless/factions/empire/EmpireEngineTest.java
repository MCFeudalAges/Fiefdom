package org.nowireless.factions.empire;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.nowireless.factions.entity.Region;

import com.massivecraft.factions.entity.Faction;

public class EmpireEngineTest {
	
	@Test
	public void canCreateEmpirePass() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		when(conf.getMinEmpirePower()).thenReturn(500.0);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		when(faction.isEmpire()).thenReturn(false);
		
		Result ret = engine.canCreateEmpire(conf, faction);
		
		assertTrue(ret.isGood());
		assertTrue(Result.PROCEED.equals(ret));
	}
	
	@Test
	public void canCreateEmpireIsEmpireFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		when(conf.getMinEmpirePower()).thenReturn(500.0);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		when(faction.isEmpire()).thenReturn(true);
		
		Result ret = engine.canCreateEmpire(conf, faction);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_IS_EMPIRE.equals(ret));
	}

	@Test
	public void canCreateEmpireIsNoneFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		when(conf.getMinEmpirePower()).thenReturn(500.0);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(true);
		when(faction.isEmpire()).thenReturn(true);
		
		Result ret = engine.canCreateEmpire(conf, faction);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_DEFAULT_FACTION.equals(ret));
	}

	
	@Test
	public void canClaimPass() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(false);
		when(region.isOwned()).thenReturn(false);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertTrue(ret.isGood());
		assertTrue(Result.PROCEED.equals(ret));
	}

	@Test
	public void canClaimNotEnoughPowerFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(500.0);
		when(faction.isNone()).thenReturn(false);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(false);
		when(region.isOwned()).thenReturn(false);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_NOT_ENOUGH_POWER.equals(ret));
	}

	@Test
	public void canClaimAllReadyOwnedFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(false);
		when(region.isOwned()).thenReturn(true);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_ALLREADY_OWNED.equals(ret));
	}

	@Test
	public void canClaimCantBeClaimedFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(true);
		when(region.isOwned()).thenReturn(false);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_CANT_BE_CLAIMED.equals(ret));
	}
	
	@Test
	public void canClaimDefaultFactionFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(true);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(false);
		when(region.isOwned()).thenReturn(false);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_DEFAULT_FACTION.equals(ret));
	}

	@Test
	public void canClaimDefaultRegionFail() {
		EmpireEngine engine = new EmpireEngine();
		
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		
		Region region = mock(Region.class);
		when(region.regionPower()).thenReturn(750.0);
		when(region.isNone()).thenReturn(true);
		when(region.isOwned()).thenReturn(false);
		when(region.isClaimable()).thenReturn(true);
		
		Result ret = engine.canClaim(conf, faction, region);
		
		assertFalse(ret.isGood());
		assertTrue(Result.NO_CANT_BE_CLAIMED.equals(ret));
	}
	
	@Test
	public void canInvitePass() {
		EmpireEngine engine = new EmpireEngine();
		EmpireConf conf = mock(EmpireConf.class);
		
		Faction faction = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		when(faction.isEmpire()).thenReturn(true);
		
		Faction faction2 = mock(Faction.class);
		when(faction.getPower()).thenReturn(1000.0);
		when(faction.isNone()).thenReturn(false);
		
		
		engine.canInvite(conf, faction, faction2);
	}

	@Test
	public void canInviteIsEmpireFail() {
		fail("Not implemented yet");
	}

	@Test
	public void canInviteIsDefaultFactionFail() {
		fail("Not implemented yet");
	}

	@Test
	public void canJoinPass() {
		fail("Not implemented yet");
	}

	@Test
	public void canJoinYouAreEmpireFail() {
		fail("Not implemented yet");
	}

	@Test
	public void canLeavePass() {
		fail("Not implemented yet");
	}

	@Test
	public void canLeaveIsEmpireFail() {
		fail("Not implemented yet");
	}

	@Test
	public void canRelationChangePass() {
		fail("Not implemented yet");
	}

	@Test
	public void canRelationChangeNoChangeFail() {
		fail("Not implemented yet");
	}

	@Test
	public void canRankChangePass() {
		fail("Not implemented yet");
	}

	@Test
	public void canRankChangeNoChangeFail() {
		fail("Not implemented yet");
	}

}
