package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unclaim extends SettlementCommand {
    public Unclaim(LocalCommand parent) {
        super(parent);
        aliases = new String[] {"unclaim"};
        usage = new String[] {
                "§2 - /settlement unclaim [settlement name]",
                "§a  \u00bbUnclaim a chunk from your currently focused Settlement, or the specified one."
        };
        shortUsage = new String[] {
                "§2 - /settlement unclaim",
                "§a  \u00bbUnclaim land for settlement."
        };
        permission = OWNER_PERMISSION + ".unclaim";
    }
    
    public boolean execute(CommandSender sender, String alias, String[] argArray) {
        if (trySubCommand(sender, alias, argArray)) {
            return true;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        CommandArguments args = CommandArguments.processArguments(alias, argArray, new String[0], new String[] {"settlement"});
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(SettlementRank.MODERATOR) || SettlementUtil.checkPermission(sender, OWNER_PERMISSION + ".unclaim", false, true)) {
            settlement.unclaimLand((Player) sender);
            return true;
        }
        else {
            settlement.sendNoRightsMessage(sender);
            return true;
        }
    }
}
