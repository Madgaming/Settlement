package net.zetaeta.settlement.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlayer;

import static org.bukkit.Bukkit.getPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class SettlementUtil extends ZPUtil implements SettlementConstants {
    
    /**
     * Checks whether a player has a particular SettlementPermission
     * 
     * @param sender Sender of the command
     * 
     * @param permission SettlementPermission to be checked
     * 
     * @return Whether the sender has the specific permission
     * */
    @Deprecated
    public static boolean checkPermission(CommandSender sender, LocalPermission permission, boolean isCommand) {
        if (checkPermissionSilent(sender, permission)) {
            return true;
        }
        if (isCommand)
            sender.sendMessage("§cYou do not have access to that command!");
        else
            sender.sendMessage("§cYou are not allowed to do that!");
        return false;
    }
    
    @Deprecated
    public static boolean checkPermissionSilent(CommandSender sender, LocalPermission permission) {
        if (sender.hasPermission(permission.getPermission()))
            return true;
        LocalPermission permIterator = permission;
        while (true) {
            if (sender.hasPermission(permIterator.getPermission() + ".*")) {
                sender.sendMessage("§a" + permIterator.getPermission() + ".*");
                return true;
            }
            sender.sendMessage(permIterator.getPermission() + ".*");
            if (permIterator.isMasterPermission()) {
                for (PermissionAttachmentInfo pai : sender.getEffectivePermissions()) {
                    sender.sendMessage(pai.getPermission());
                }
                break;
            }
            permIterator = permIterator.getParent();
        }
        return false;
    }
    
    
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
    
    public static boolean checkCommandValid(CommandSender sender, LocalPermission permission) {
        if (!checkPermission(sender, permission, true)) {
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
}
