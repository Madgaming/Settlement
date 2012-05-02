package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;
import net.zetaeta.settlement.util.SubCommandable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SubCommandable
public class SFocus extends SettlementCommand {
    
    {
        usage = new String[] {
                "§2 - /settlement focus <settlement name>",
                "§a  Focus your commands on a specific Settlement, to avoid having to type out its name multiple times.",
                "§a  If you are a member of only one Settlement, your focus will be set to that by default."
        };
        aliases = new String[] {"focus"};
        permission = new SettlementPermission("focus", SettlementPermission.USE_BASIC_PERMISSION);
    }
    
    public SFocus(LocalCommandExecutor parent) {
        super(parent);
        registerSubCommand(new FocusOff(this));
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (doSubCommand(sender, alias, args)) {
            return true;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
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
            if (doSubCommand(sender, alias, args)) {
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
