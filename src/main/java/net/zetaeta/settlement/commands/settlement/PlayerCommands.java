package net.zetaeta.settlement.commands.settlement;

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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements LocalCommandExecutor, SettlementConstants {
    
    @Command(aliases = {"join", "enter"},
            usage = {"§2 - /settlement join <settlement name>",
            "§a  Join the specified settlement if you have a pending invite."},
            shortUsage = {"§2 - /settlement join",
            "§a  \u00bbJoin a settlement."},
            permission = SettlementCommand.BASIC_PERMISSION + ".join",
            checkPermissions = true,
            playersOnly = true)
    public boolean join(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (target == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_BASIC_PERMISSION + ".join", false, true)) {
            target.addMember(sPlayer);
            if (args.hasBooleanFlag("silent") || args.hasBooleanFlag("s")) {
                SettlementMessenger.sendSettlementMessage(sender, StringUtil.concatString(42 + 16, "§a  You joined the Settlement §6", target.getName(), " §asilently"));
                return true;
            }
            target.broadcastSettlementMessage(StringUtil.concatString(33 + 16, "§b  ", sPlayer.getName(), " §ahas joined the Settlement!"));
            return true;
        }
        if (target.isInvited(sPlayer)) {
            target.addMember(sPlayer);
            target.broadcastSettlementMessage(StringUtil.concatString(33 + 16, "§b  ", sPlayer.getName(), " §ahas joined the Settlement!"));
            return true;
        }
        else {
            SettlementMessenger.sendSettlementMessage(sender, "§c  You have not been invited to the Settlement " + target.getName());
            target.broadcastSettlementMessage(StringUtil.concatString(0, "§6  ", sPlayer.getName(), " §atried to join the Settlement!"));
            return true;
        }
    }
    
    @Command(aliases = {"leave", "quit", "exit"},
            usage = {"§2 - /settlement leave [settlement name]",
            "§a  \u00bbLeave the specified settlement, or your currently focused one if you have one."},
            shortUsage = {"§2 - /settlement leave",
            "§a  \u00bbLeave a settlement."},
            permission = SettlementCommand.BASIC_PERMISSION + ".leave",
            useCommandArguments = true,
            playersOnly = true)
    public boolean leave(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement from = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (from == null) {
            return true;
        }
        if (from.isMember(sPlayer)) {
            if (PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_BASIC_PERMISSION + ".leave", false, true)) {
                if (args.hasBooleanFlag("silent") || args.hasBooleanFlag("s")) {
                    from.removeMember(sPlayer);
                    SettlementMessenger.sendSettlementMessage(sender, StringUtil.concatString(40 + 16, "  §a  You left the Settlement §6", from.getName(), " §asilently"));
                    return true;
                }
            }
            if (sPlayer.getRank(from) == Rank.OWNER && from.getMemberCount() > 1) {
                SettlementMessenger.sendSettlementMessage(sender, "§c  You are not allowed to do that!", "§a  As §bowner §a, you may not leave the settlement without first deleting it!", "§a  If you are sure, use /settlement delete!");
            }
            from.removeMember(sPlayer);
            SettlementMessenger.sendSettlementMessage(sender, StringUtil.concatString(40 + 16, "  §a  You left the Settlement §6", from.getName(), " §a!"));
            from.broadcastSettlementMessage(StringUtil.concatString(0, "  §b", sPlayer.getName(), " §aleft the Settlement!"));
            return true;
        }
        SettlementMessenger.sendSettlementMessage(sender, "§c  You are not in that Settlement!");
        return true;
    }
}
