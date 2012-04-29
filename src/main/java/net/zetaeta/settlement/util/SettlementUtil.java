package net.zetaeta.settlement.util;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public abstract class SettlementUtil extends ZPUtil {
    
    protected SettlementUtil() { }
    
    /**
     * Checks whether a player has a particular SettlementPermission
     * 
     * @param sender Sender of the command
     * 
     * @param permission SettlementPermission to be checked
     * 
     * @return Whether the sender has the specific permission
     * */
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
    
    public static Settlement getOwner(Chunk chunk) {
        for (Settlement settlement : Settlement.getSettlements()) {
            if (settlement.ownsChunk(chunk)) {
                return settlement;
            }
        }
        return null;
    }
}
