package org.nowireless.factions.empire;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nowireless.factions.cmd.CmdEmpireCreate;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.util.PermUtil;
import com.massivecraft.massivecore.util.Txt;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, UPlayer.class, MPlayer.class, UConf.class, Faction.class, Factions.class, PermUtil.class, Mixin.class})
public class CmdEmpireCreateTest {

	private boolean isEmpire;
	
	@Test
	public void test() {
		PowerMockito.mockStatic(Factions.class);
		PowerMockito.mockStatic(MPlayer.class);
		PowerMockito.mockStatic(UPlayer.class);
		PowerMockito.mockStatic(UConf.class);
		PowerMockito.mockStatic(PermUtil.class);
		PowerMockito.mockStatic(Mixin.class);
		Factions factions = Mockito.mock(Factions.class);
		MPlayer mPlayer = Mockito.mock(MPlayer.class);
		final UPlayer uPlayer = Mockito.mock(UPlayer.class);
		Faction faction = Mockito.mock(Faction.class);
		Player player = Mockito.mock(Player.class);
		
		UConf uConf = new UConf();
		uConf.minEmpirePower = 500;
		Mockito.when(faction.getPower()).thenReturn(1000.0);
		Mockito.when(faction.isEmpire()).thenReturn(false);
		Mockito.when(Factions.get()).thenReturn(factions);
		Mockito.when(MPlayer.get(Mockito.any())).thenReturn(mPlayer);
		Mockito.when(UPlayer.get(Mockito.any())).thenReturn(uPlayer);
		Mockito.when(uPlayer.getFaction()).thenReturn(faction);
		Mockito.when(uPlayer.hasFaction()).thenReturn(true);
		Mockito.when(uPlayer.getRole()).thenReturn(Rel.LEADER);
		Mockito.when(UConf.isDisabled(Mockito.any())).thenReturn(false);
		Mockito.when(UConf.get(Mockito.any())).thenReturn(uConf);
		Mockito.when(PermUtil.getDescription(Perm.EMPIRE_CREATE.node)).thenReturn("Create a empire");
		Mockito.when(PermUtil.getDeniedMessage(Perm.EMPIRE_CREATE.node)).thenReturn("You do not have premission to create a empire");
		
		isEmpire = false;
		
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String msg = Txt.implode(args, " ");
				System.out.println("[Console] " + ChatColor.stripColor(msg));
				return null;
			}
		}).when(factions).log(Mockito.any());
		
		Answer<Void> uPlayMsgAnswer = new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				System.out.println("[UPlayer] " + ChatColor.stripColor(Txt.implode(invocation.getArguments(), " ")));
				return null;
			}
		};
		Mockito.doAnswer(uPlayMsgAnswer).when(uPlayer).sendMessage(Mockito.anyString());
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("unchecked")
				Collection<String> coll = (Collection<String>) invocation.getArguments()[0];
				System.out.println("[UPlayer] " + ChatColor.stripColor(Txt.implode(coll, " ")));
				return null;
			}
		}).when(uPlayer).sendMessage(Mockito.anyCollectionOf(String.class));
		
		Mockito.doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				String perm  = (String) invocation.getArguments()[0];
				return Perm.EMPIRE_CREATE.node.equalsIgnoreCase(perm); 
			}
		}).when(player).hasPermission(Mockito.anyString());
		
		PowerMockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				String msg = (String) invocation.getArguments()[1];
				uPlayer.sendMessage(msg);
				return null;
			}
		}).when(Mixin.class);
		
		Mixin.msgOne(Mockito.anyObject(), Mockito.anyString());
		PowerMockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				String msg = (String) invocation.getArguments()[1];
				uPlayer.sendMessage(msg);
				return null;
			}
		}).when(Mixin.class);
		Mixin.messageOne(Mockito.anyObject(), Mockito.anyString());	
		
		PowerMockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("unchecked")
				Collection<String> msg = (Collection<String>) invocation.getArguments()[1];
				uPlayer.sendMessage(msg);
				return null;
			}
		}).when(Mixin.class);
		Mixin.messageOne(Mockito.anyObject(), Mockito.anyCollectionOf(String.class));
		
		
		Mockito.doAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				System.out.println(ChatColor.stripColor((String) invocation.getArguments()[0]));
				return null;
			}
		}).when(faction).sendMessage(Mockito.anyString());
		
		Mockito.doAnswer(new Answer<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				System.out.println(ChatColor.stripColor(Txt.implode((Collection<String>) invocation.getArguments()[0], " ")));
				return null;
			}
		}).when(faction).sendMessage(Mockito.anyCollectionOf(String.class));

		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Boolean isEmpire = (Boolean) invocation.getArguments()[0];
				CmdEmpireCreateTest.this.isEmpire = isEmpire;
				return null;
			}
		}).when(faction).setEmpire(Mockito.anyBoolean());
		
		CmdEmpireCreate cmd = new CmdEmpireCreate();
		
		List<String> args = new ArrayList<String>();
		cmd.execute(player, args);
		
		assertTrue(isEmpire);
	}

}
