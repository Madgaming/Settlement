package net.zetaeta.settlement.listeners;

import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementThreadManager;
import net.zetaeta.settlement.object.ChunkCoordinate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class SettlementWorldListener implements Listener, SettlementConstants {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {
        log.info("Loaded chunk!");
        SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                server.getWorld(event.getWorld()).loadPlot(new ChunkCoordinate(event.getChunk()));
            }
        });
    }
    
    public void onChunkUnload(final ChunkUnloadEvent event) {
        log.info("Unloaded chunk!");
        SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                server.getWorld(event.getWorld()).unloadPlot(new ChunkCoordinate(event.getChunk()));
            }
        });
    }
}
