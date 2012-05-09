package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.settlement.util.SettlementMessenger.sendSettlementMessage;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.util.PermissionUtil;
import net.zetaeta.libraries.util.StringUtil;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModeratorCommands implements LocalCommandExecutor, SettlementConstants {
    
    @Command(aliases = {"invite", "add"},
            usage = {"§2 - /settlement invite <player>",
            "§a  \u00bbInvite <player> to the settlement"},
            shortUsage = {"§2 - /settlement invite",
            "§a  \u00bbInvite a player to a settlement."},
            permission = SettlementCommand.OWNER_PERMISSION + ".invite",
            boolFlags = {"e", "exact"},
            valueFlags = {"settlement"},
            checkPermissions = true,
            playersOnly = true,
            useCommandArguments = true)
    public boolean invite(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = null;
        String[] newArgs = args.getUnprocessedArgArray();
        if (newArgs.length < 1) {
            return false;
        }
        sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement invitedTo = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (invitedTo != null) {
            if (!sPlayer.getRank(invitedTo).isEqualOrSuperiorTo(Rank.MODERATOR) && !PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_OWNER_PERMISSION + ".invite", true, true)) {
                invitedTo.sendNoRightsMessage(sender);
                return true;
            }
            if (args.hasBooleanFlag("e") || args.hasBooleanFlag("exact")) { // exact name
                
                invitedTo.addInvitation(newArgs[0]);
                sendSettlementMessage(sender, StringUtil.concatString("§b  You §ahave invited ", newArgs[0], " to your Settlement, ", invitedTo.getName()));
                if (Bukkit.getPlayerExact(newArgs[0]) != null) {
                    sendSettlementMessage(Bukkit.getPlayerExact(newArgs[0]), StringUtil.concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                }
                return true;
            }
            if (newArgs.length == 1) {
                if (Bukkit.getPlayer(newArgs[0]) != null) {
                    Player invitee = Bukkit.getPlayer(newArgs[0]);
                    invitedTo.addInvitation(invitee.getName());
                    sendSettlementMessage(sender, StringUtil.concatString("§b  You §2have invited §b", invitee.getName(), " §2to your Settlement, §6", invitedTo.getName(), "§2!"));
                    sendSettlementMessage(invitee, StringUtil.concatString("§b  ", sender.getName(), " §2has invited you to the Settlement §6", invitedTo.getName(), "§2!"));
                    return true;
                }
                sender.sendMessage("§cNot an online player!");
                return true;
            }
            
        }
        return false;
    }
    
    @Command(aliases = {"kick", "remove"},
            usage = {"§2 - /settlement kick <player>",
            "§a  \u00bbKick <player> from the settlement"},
            shortUsage = {"§2 - /settlement kick",
            "§a  \u00bbKick a player from a settlement."},
            permission = SettlementCommand.OWNER_PERMISSION + ".kick",
            boolFlags = {"exact", "e"},
            valueFlags = {"settlement"},
            checkPermissions = true,
            useCommandArguments = true)
    public boolean kick(CommandSender sender, CommandArguments args) {
        if (args.getRawArgs().length == 0) {
            return false;
        }
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement settlement = SettlementUtil.getFocusedOrSpecified(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
        }
        if (!sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.MODERATOR) && !PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_OWNER_PERMISSION + ".kick", false, true)) {
            settlement.sendNoRightsMessage(sender);
            return true;
        }
        if (args.hasBooleanFlag("e") || args.hasBooleanFlag("exact")) {
            String target = StringUtil.arrayAsString(args.getUnprocessedArgArray());
            if ((settlement.getModeratorNames().contains(target) && !(sPlayer.getRank(settlement) == Rank.OWNER)) || settlement.getOwnerName().equalsIgnoreCase(target)) {
                settlement.sendNoRightsMessage(sender);
                return true;
            }
            settlement.removeMember(target);
            return true;
        }
        else {
            SettlementPlayer target = settlement.getMember(StringUtil.arrayAsString(args.getUnprocessedArgArray()));
            if (target == null) {
                SettlementMessenger.sendInvalidPlayerMessage(sender);
                return true;
            }
            if ((target.getRank(settlement) == Rank.MODERATOR && !(sPlayer.getRank(settlement) == Rank.OWNER)) || target.getRank(settlement) == Rank.OWNER) {
                settlement.sendNoRightsMessage(sender);
                return true;
            }
            settlement.removeMember(target);
            return true;
        }
    }
}
