package net.zetaeta.settlement.listeners;

import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SettlementPlayerListener implements Listener, SettlementConstants {
    
    @SuppressWarnings("static-method")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        log.info("PlayerLogin: " + event.getPlayer().getName());
        if(!SettlementPlayer.playerMap.containsKey(event.getPlayer())) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(SettlementPlugin.plugin, new Runnable() {
                public void run() { 
                    new SettlementPlayer(event.getPlayer()).register(); 
                }
            });
        }
        else {
            event.getPlayer().sendMessage("§4Settlement Error: Please log out and back in to avoid player data corruption.");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerQuitEvent event) {
        log.info("Player logged out");
        SettlementPlayer.getSettlementPlayer(event.getPlayer()).unregister();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().equals(event.getTo())) {
            return;
        }
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            return;
        }
        Chunk to = event.getTo().getChunk();
        Settlement owner = SettlementUtil.getOwner(to);
        String slog = owner.getSlogan();
    }
    
}
