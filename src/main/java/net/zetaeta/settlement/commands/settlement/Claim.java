package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Claim extends SettlementCommand {
    
    public Claim(LocalCommand parent) {
        super(parent);
        aliases = new String[] {"claim", "acquire", "reappropriate"};
        usage = new String[] {
                "§2 - /settlement claim [settlement name]",
                "§a  \u00bbClaim a chunk for your currently focused Settlement, or the specified one."
        };
        shortUsage = new String[] {
                "§2 - /settlement claim",
                "§a  \u00bbClaim land for settlement."
        };
        permission = OWNER_PERMISSION + ".claim";
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        CommandArguments cArgs = CommandArguments.processArguments(alias, args, new String[0], new String[] {"settlement"});
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement set = SettlementUtil.getFocusedOrStated(sPlayer, cArgs);
        if (set == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (SettlementUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".claim", false, true) || sPlayer.getRank(set).isEqualOrSuperiorTo(SettlementRank.MODERATOR)) {
            set.claimLand((Player) sender);
            return true;
        }
        else {
            set.sendNoRightsMessage(sender);
            return true;
        }
        
    }
}
