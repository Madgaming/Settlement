package net.zetaeta.settlement.listeners;

import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;

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
        if(server.getSettlementPlayer(event.getPlayer()) == null) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(SettlementPlugin.plugin, new Runnable() {
                public void run() {
                    server.registerPlayer(new SettlementPlayer(event.getPlayer()));
                }
            });
        }
        else {
            event.getPlayer().sendMessage("�4Settlement Error: Please log out and back in to avoid player data corruption.");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerQuitEvent event) {
        server.unregisterPlayer(server.getSettlementPlayer(event.getPlayer()));
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
        Chunk from = event.getFrom().getChunk();
        Settlement owner = server.getOwner(to);
        Settlement prev = server.getOwner(from);
        if (owner == prev) {
            return;
        }
        if (owner == null) {
            SettlementMessenger.sendWildernessMessage(event.getPlayer());
            return;
        }
        SettlementMessenger.sendPlotChangeMessage(event.getPlayer(), owner);
    }
    
}
