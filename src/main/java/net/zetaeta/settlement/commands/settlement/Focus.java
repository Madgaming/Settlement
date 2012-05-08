package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;
import net.zetaeta.settlement.util.SubCommandable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SubCommandable
public class Focus extends SettlementCommand {
    
    {
        usage = new String[] {
                "§2 - /settlement focus <settlement name>",
                "§a  \u00bbFocus your commands on a specific Settlement, to avoid having to type out its name multiple times.",
                "§a  \u00bbIf you are a member of only one Settlement, your focus will be set to that by default."
        };
        shortUsage = new String[] {
                "§2 - /settlement focus",
                "§a  \u00bbFocus on a specific Settlement."
        };
        aliases = new String[] {"focus"};
        permission = BASIC_PERMISSION + ".focus";
    }
    
    public Focus(LocalCommand parent) {
        super(parent);
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        CommandArguments cArgs = CommandArguments.processArguments(alias, args, new String[] {"off"}, new String[] {"settlement"});
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        if (args.length == 0 || cArgs.hasBooleanFlag("off")) {
            SettlementMessenger.sendSettlementMessage(sender, "§2  Focus turned off!");
            sPlayer.setFocus(null);
        }
        Settlement focus = SettlementUtil.getStated(cArgs);
        if (focus == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        sPlayer.setFocus(focus);
        SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement focus set to " + focus.getName());
        return true;
    }
}
