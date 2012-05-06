package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
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
        registerSubCommand(new FocusOff(this));
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        if (args.length == 0) {
            SettlementMessenger.sendSettlementMessage(sender, "§2  Focus turned off!");
            sPlayer.setFocus(null);
        }
        Settlement focus = Settlement.getSettlement(ZPUtil.arrayAsString(args));
        if (focus == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        sPlayer.setFocus(focus);
        SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement focus set to " + focus.getName());
        return true;
    }
    
    @SubCommandable
    public static class FocusOff extends SettlementCommand {
        
        public FocusOff(SettlementCommand parent) {
            super(parent);
            usage = parent.getUsage();
            permission = parent.getPermission();
            aliases = new String[] {"off", "disable"};
            parent.registerSubCommand(this);
        }
        
        @Override
        public boolean execute(CommandSender sender, String alias, String[] args) {
            if (trySubCommand(sender, alias, args)) {
                return true;
            }
            if (!SettlementUtil.checkCommandValid(sender, permission)) {
                return true;
            }
            SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
            sPlayer.setFocus(null);
            SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement focus disabled!");
            return true;
        }
    }
}
