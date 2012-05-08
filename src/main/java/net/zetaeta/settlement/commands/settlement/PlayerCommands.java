package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements LocalCommandExecutor {
    
    @Command(aliases = {"join", "enter"},
            usage = {"§2 - /settlement join <settlement name>",
            "§a  Join the specified settlement if you have a pending invite."},
            shortUsage = {"§2 - /settlement join",
            "§a  \u00bbJoin a settlement."},
            permission = SettlementCommand.BASIC_PERMISSION + ".join")
    public boolean join(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (target == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (SettlementUtil.checkPermission(sender, SettlementCommand.ADMIN_BASIC_PERMISSION + ".join", false, true)) {
            target.addMember(sPlayer);
            if (args.hasBooleanFlag("silent") || args.hasBooleanFlag("s")) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(42 + 16, "§a  You joined the Settlement §6", target.getName(), " §asilently"));
                return true;
            }
            target.broadcastSettlementMessage(SettlementUtil.concatString(33 + 16, "§b  ", sPlayer.getName(), " §ahas joined the Settlement!"));
            return true;
        }
        if (target.isInvited(sPlayer)) {
            target.addMember(sPlayer);
            target.broadcastSettlementMessage(SettlementUtil.concatString(33 + 16, "§b  ", sPlayer.getName(), " §ahas joined the Settlement!"));
            return true;
        }
        else {
            SettlementMessenger.sendSettlementMessage(sender, "§c  You have not been invited to the Settlement " + target.getName());
            target.broadcastSettlementMessage(SettlementUtil.concatString(0, "§6  ", sPlayer.getName(), " §atried to join the Settlement!"));
            return true;
        }
    }
}
