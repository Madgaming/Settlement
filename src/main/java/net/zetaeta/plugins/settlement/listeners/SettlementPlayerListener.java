package net.zetaeta.plugins.settlement.listeners;

import net.zetaeta.plugins.settlement.Databases;
import net.zetaeta.plugins.settlement.SettlementPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SettlementPlayerListener implements Listener {
	
	@SuppressWarnings("static-method")
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerLogin(PlayerLoginEvent event) {
		if(!SettlementPlayer.playerMap.containsKey(event.getPlayer()))
		new SettlementPlayer(event.getPlayer());
	}
	
}
