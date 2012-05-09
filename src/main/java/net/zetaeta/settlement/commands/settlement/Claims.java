package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.util.PermissionUtil;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Claims implements LocalCommandExecutor, SettlementConstants {
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"claim", "acquire", "reappropriate"},
            usage = {"§2 - /settlement claim [settlement name]",
                     "§a  \u00bbClaim a chunk for your currently focused Settlement, or the specified one."},
            shortUsage = {"§2 - /settlement claim",
                          "§a  \u00bbClaim land for settlement."
            },
            permission = SettlementCommand.OWNER_PERMISSION + ".claim",
            valueFlags = {"settlement"},
            useCommandArguments = true,
            playersOnly = true)
    public boolean claim(CommandSender sender, CommandArguments args) {
        if (!SettlementUtil.checkCommandValid(sender, SettlementCommand.OWNER_PERMISSION + ".claim")) {
            return true;
        }
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement set = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (set == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_OWNER_PERMISSION + ".claim", false, true) || sPlayer.getRank(set).isEqualOrSuperiorTo(Rank.MODERATOR)) {
            set.claimLand((Player) sender);
            return true;
        }
        else {
            set.sendNoRightsMessage(sender);
            return true;
        }
    }
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"unclaim"},
            usage = {"§2 - /settlement unclaim [settlement name]",
                    "§a  \u00bbUnclaim a chunk from your currently focused Settlement, or the specified one."},
            shortUsage = {"§2 - /settlement unclaim",
                    "§a  \u00bbUnclaim land for settlement."},
            permission = SettlementCommand.OWNER_PERMISSION + ".unclaim",
            valueFlags = {"settlement"},
            useCommandArguments = true,
            playersOnly = true)
    public boolean unClaim(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.MODERATOR) || PermissionUtil.checkPermission(sender, SettlementCommand.OWNER_PERMISSION + ".unclaim", false, true)) {
            settlement.unclaimLand((Player) sender);
            return true;
        }
        else {
            settlement.sendNoRightsMessage(sender);
            return true;
        }
    }
    
}
