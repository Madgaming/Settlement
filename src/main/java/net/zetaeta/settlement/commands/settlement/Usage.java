package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class Usage extends SettlementCommand {
    
    public Usage(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - " + parent.toString() + " usage <command>:",
                "§a  \u00bbGet the usage information for the command <command>.",
                "§a  \u00bbFor a list of all commands, use /settlement help [page]"
        };
        shortUsage = new String[] {
                "§2 - " + parent.toString() + " usage",
                "§a  \u00bbGet usage info on a command"
        };
        aliases = new String[] {"usage"};
        permission = BASIC_PERMISSION + ".usage";
    }
    
    @SuppressWarnings("static-access")
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        String commandName = args[0];
        LocalCommand subCommand;
        if ((subCommand = parent.getSubCommand(commandName)) == null) {
            SettlementMessenger.sendSettlementMessage(sender, "§c  There is no command of that name!");
            return true;
        }
        SettlementMessenger.sendUsage(sender, subCommand.getUsage());
        return true;
    }
}
