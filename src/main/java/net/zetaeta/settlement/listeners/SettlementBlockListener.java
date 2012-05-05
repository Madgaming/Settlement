package net.zetaeta.settlement.listeners;

import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SettlementBlockListener implements Listener, SettlementConstants {
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Settlement plotOwner = null;
        if ((plotOwner = SettlementUtil.getOwner(event.getBlock().getChunk())) == null) {
            return;
        }
        if (plotOwner.isMember(event.getPlayer().getName())) {
            return;
        }
        event.getPlayer().sendMessage(ConfigurationConstants.denyBuildMessage.replace("%s", plotOwner.getName()));
    }
    
}
