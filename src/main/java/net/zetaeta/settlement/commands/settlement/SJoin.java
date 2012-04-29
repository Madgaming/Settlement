package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SJoin extends SettlementCommand {
    
    {
        usage = new String[] {
                "§2 - /settlement join <settlement name>",
                "§a  Join the specified settlement if you have a pending invite."
        };
        permission = new SettlementPermission("join", SettlementPermission.USE_BASIC_PERMISSION);
        aliases = new String[] {"join", "enter"};
    }
    
    public SJoin(LocalCommandExecutor parent) {
        super(parent);
        parent.registerSubCommand(this);
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args, true);
        if (target.isInvited(sPlayer)) {
            target.addMember(sPlayer);
            target.broadcastSettlementMessage(SettlementUtil.concatString(33 + 16, "§6  ", sPlayer.getName(), " §ahas joined the Settlement!"));
            return true;
        }
        else {
            SettlementMessenger.sendSettlementMessage(sender, "§c  You have not been invited to the Settlement " + target.getName());
            return true;
        }
    }
}
