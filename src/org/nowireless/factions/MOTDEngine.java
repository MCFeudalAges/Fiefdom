package org.nowireless.factions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.util.Txt;

public class MOTDEngine extends EngineAbstract {

	private static MOTDEngine i = new MOTDEngine();
	public static MOTDEngine get() { return i; }
	
	@Override public Plugin getPlugin() { return Factions.get(); }
	
	private Map<UUID, Long> playerLoginTimes = new HashMap<UUID, Long>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Factions.get().log(Txt.parse("<i>Before add: <v>" + playerLoginTimes.toString()));
		playerLoginTimes.put(player.getUniqueId(), System.currentTimeMillis());
		Factions.get().log(Txt.parse("<i>After add: <v>" + playerLoginTimes.toString()));
		MPlayer mPlayer = MPlayer.get(player);
		Bukkit.getScheduler().runTaskLater(Factions.get(), new MOTDRunnable(mPlayer), 20);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeaveEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Factions.get().log(Txt.parse("<i>Before remove: <v>" + playerLoginTimes.toString()));
		playerLoginTimes.remove(player.getUniqueId());
		Factions.get().log(Txt.parse("<i>After remove: <v>" + playerLoginTimes.toString()));
	}

	private class MOTDRunnable implements Runnable {

		private final MPlayer player;
		
		public MOTDRunnable(MPlayer player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			
			List<String> msg = new ArrayList<String>();
			msg.add(Txt.titleize("Online Players"));
			for(Entry<UUID, Long> entry : playerLoginTimes.entrySet()) {
				Player player = Bukkit.getPlayer(entry.getKey());
				Date loginDate = new Date(entry.getValue());
				msg.add(Txt.parse("<v>" + player.getDisplayName() + "<i> on since <a>"+ loginDate.toString()));
				
			}
			player.sendMessage(msg);
		}
	}
	
}
