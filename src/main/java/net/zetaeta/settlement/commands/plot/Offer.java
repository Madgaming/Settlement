package net.zetaeta.settlement.commands.plot;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Offer extends SettlementCommand {
    public Offer(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                
        };
        shortUsage = new String[] {
                
        };
        permission = PLOT_PERMISSION + ".offer";
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
    }
}
