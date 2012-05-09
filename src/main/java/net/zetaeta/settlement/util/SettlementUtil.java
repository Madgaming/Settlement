package net.zetaeta.settlement.util;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.util.PermissionUtil;
import net.zetaeta.libraries.util.StringUtil;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettlementUtil implements SettlementConstants {
    
    public static Settlement getFocusedOrStated(SettlementPlayer player, String[] args, boolean sendError) {
        Settlement sm = null;
        if ((sm = server.getSettlement(StringUtil.arrayAsString(args))) != null) {
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
            return server.getSettlement(args.getFlagValue("settlement"));
        }
        if ((returned = plugin.getSettlementServer().getSettlement(StringUtil.arrayAsString(args.getUnprocessedArgArray()))) != null) {
            return returned;
        }
        return player.getFocus();
    }
    
    public static Settlement getStated(CommandArguments args) {
        if (args.hasFlagValue("settlement")) {
            return server.getSettlement(args.getFlagValue("settlement"));
        }
        return plugin.getSettlementServer().getSettlement(StringUtil.arrayAsString(args.getUnprocessedArgArray()));
    }
    
    public static Settlement getFocusedOrSpecified(SettlementPlayer player, CommandArguments args) {
        if (args.hasFlagValue("settlement")) {
            return server.getSettlement(args.getFlagValue("settlement"));
        }
        return player.getFocus();
    }
    
    public static boolean checkCommandValid(CommandSender sender, String permission) {
        if (!PermissionUtil.checkPermission(sender, permission, true, true)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be run by a player!");
            return false;
        }
        return true;
    }
}
