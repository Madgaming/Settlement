package net.zetaeta.settlement.commands;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.AbstractLocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class SettlementCommand extends AbstractLocalCommandExecutor {
	protected SettlementPermission permission;
    
    public SettlementCommand(LocalCommandExecutor parent) {
        super(parent);
    }
    
    public SettlementCommand(LocalCommandExecutor parent, LocalPermission permission, String[] usage, String[] aliases) {
        super(parent, permission, usage, aliases);
    }
    
    @Override
    public SettlementPermission getPermission() {
        return permission;
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (args.length > 0) {
            for (String s : subCommands.keySet()) {
                if (s.equalsIgnoreCase(args[0])) {
                    return subCommands.get(args[0]).execute(sender, alias, ZPUtil.removeFirstIndex(args));
                }
            }
            return false;
        }
        return false;
    }
    
    /**
     * Finds and executes any required subcommands, sending the sender the command's usage if the subcommand returns true.
     * 
     * @param sender Sender of the command to pass to the subcommand
     * @param alias Current command's alias.
     * @param args Arguments of the current command.
     * @return True if a subcommand is run, false otherwise. It is recommended for the current command to return true if this method returns true.
     */
    @SuppressWarnings("static-access")
    public boolean doSubCommand(CommandSender sender, String alias, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (subCommands.containsKey(args[0])) {
            if (subCommands.get(args[0]).execute(sender, args[0], SettlementUtil.removeFirstIndex(args))) {
                return true;
            }
            else {
                SettlementMessenger.sendUsage(sender, subCommands.get(args[0]).getUsage());
                return true;
            }
        }
        return false;
    }
}
