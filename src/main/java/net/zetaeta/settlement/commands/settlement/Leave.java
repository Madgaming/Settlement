package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends SettlementCommand {
    
    {
        usage = new String[] {
                "§2 - /settlement leave [settlement name]",
                "§a  \u00bbLeave the specified settlement, or your currently focused one if you have one."
        };
        shortUsage = new String[] {
                "§2 - /settlement leave",
                "§a  \u00bbLeave a settlement."
        };
        aliases = new String[] {"leave", "quit", "exit"};
        permission = BASIC_PERMISSION + ".leave";
    }
    
    public Leave(LocalCommand parent) {
        super(parent);
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        CommandArguments arguments = CommandArguments.processArguments(alias, args, new String[] {"silent", "s"}, new String[] {"settlement"});
        if (arguments == null)
            return true;
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement from = SettlementUtil.getFocusedOrStated(sPlayer, arguments);
        if (from == null) {
            return true;
        }
        if (from.isMember(sPlayer)) {
            if (SettlementUtil.checkPermission(sender, ADMIN_BASIC_PERMISSION + ".leave", false, true)) {
                if (arguments.hasBooleanFlag("silent") || arguments.hasBooleanFlag("s")) {
                    from.removeMember(sPlayer);
                    SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(40 + 16, "  §a  You left the Settlement §6", from.getName(), " §asilently"));
                    return true;
                }
            }
            from.removeMember(sPlayer);
            SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(40 + 16, "  §a  You left the Settlement §6", from.getName(), " §a!"));
            from.broadcastSettlementMessage(SettlementUtil.concatString(0, "  §b", sPlayer.getName(), " §aleft the Settlement!"));
            return true;
        }
        SettlementMessenger.sendSettlementMessage(sender, "§c  You are not in that Settlement!");
        return true;
    }
}
