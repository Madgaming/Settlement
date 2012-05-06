package net.zetaeta.settlement.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlayer;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettlementUtil extends ZPUtil implements SettlementConstants {
    
    
    public static Settlement getFocusedOrStated(SettlementPlayer player, String[] args, boolean sendError) {
        Settlement sm = null;
        if ((sm = Settlement.getSettlement(arrayAsString(args))) != null) {
            return sm;
        }
        if ((sm = player.getFocus()) != null) {
            return sm;
        }
        if (sendError) {
            if (args.length > 0) {
                SettlementMessenger.sendInvalidSettlementMessage(player.getPlayer());
            }
            else {
                SettlementMessenger.sendNoFocusMessage(player.getPlayer());
            }
        }
        return null;
    }
    
    public static Settlement getFocusedOrStated(SettlementPlayer player, CommandArguments args) {
        Settlement returned;
        if (args.hasFlagValue("settlement")) {
            return Settlement.getSettlement(args.getFlagValue("settlement"));
        }
        if ((returned = Settlement.getSettlement(arrayAsString(args.getUnprocessedArgArray()))) != null) {
            return returned;
        }
        return player.getFocus();
    }
    
    public static Settlement getStated(CommandArguments args) {
        Settlement returned;
        if (args.hasFlagValue("settlement")) {
            return Settlement.getSettlement(args.getFlagValue("settlement"));
        }
        return Settlement.getSettlement(arrayAsString(args.getUnprocessedArgArray()));
    }
    
    public static boolean checkCommandValid(CommandSender sender, String permission) {
        if (!checkPermission(sender, permission, true, true)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be run by a player!");
            return false;
        }
        return true;
    }
    
    private static Map<Chunk, Settlement> settlementCache;
    
    public static Settlement getOwner(Chunk chunk) {
        if (ConfigurationConstants.useChunkOwnershipCacheing) {
            if (settlementCache == null) {
                settlementCache = new HashMap<Chunk, Settlement>((int) (ConfigurationConstants.chunkOwnershipCacheSize / 0.75));
            }
            if (settlementCache.containsKey(chunk)) {
                return settlementCache.get(chunk);
            }
        }
        if (ConfigurationConstants.useSettlementWorldCacheing) {
            for (Settlement settlement : Settlement.getSettlementsIn(chunk.getWorld())) {
                if (settlement.ownsChunk(chunk)) {
                    if (ConfigurationConstants.useChunkOwnershipCacheing) {
                        settlementCache.put(chunk, settlement);
                    }
                    return settlement;
                }
            }
            return null;
        }
        else {
            for (Settlement settlement : Settlement.getSettlements()) {
                if (settlement.ownsChunk(chunk)) {
                    if (ConfigurationConstants.useChunkOwnershipCacheing) {
                        settlementCache.put(chunk, settlement);
                    }
                    return settlement;
                }
            }
            return null;
        }
    }
    
    public static void clearFromChunkCache(Settlement settlement) {
        for (Iterator<Settlement> setIt = settlementCache.values().iterator(); setIt.hasNext();) {
            if (setIt.next().equals(settlement)) {
                setIt.remove();
            }
        }
    }
}
