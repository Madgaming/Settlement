package net.zetaeta.settlement.commands;

import static net.zetaeta.libraries.ZPUtil.removeFirstIndex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.DynamicCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.commands.settlement.SConfirm;
import net.zetaeta.settlement.commands.settlement.SCreate;
import net.zetaeta.settlement.commands.settlement.SDelete;
import net.zetaeta.settlement.commands.settlement.SInfo;
import net.zetaeta.settlement.commands.settlement.SInvite;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SettlementCommandsManager extends DynamicCommandExecutor implements LocalCommandExecutor {
    
    private final String[] aliases = {};
    private Map<String, LocalCommandExecutor> subCommands = new HashMap<String, LocalCommandExecutor>();
    private String[] usage;
    public static SettlementCommandsManager settlementCommandsManager;
    
    
    {
        usage = new String[] {"settlement"};
    }
    
    {
        new SCreate(this);
        new SDelete(this);
        new SInvite(this);
        new SInfo(this);
        new SConfirm(this);
    }
    
    public SettlementCommandsManager() {
        settlementCommandsManager = this;
    }
    
    /**
     * Runs the standard settlement command (/settlement) and all possible arguments that follow it.
     * 
     * @param sender Sender of the command
     * 
     * @param command Command to be run.
     * 
     * @param cmdlbl Alias of command used.
     * 
     * @param args Arguments passed to the command in array form.
     * 
     * @return Whether command completes (unused).
     * */
    
    @net.zetaeta.libraries.commands.Command("settlement")
    public boolean settlementCommand(CommandSender sender, Command command, String cmdlbl, String[] args) {
        SettlementPlugin.log.info("settlementCommand: " + sender.getName() + ", " + command.getName() + ", " + cmdlbl + ZPUtil.arrayAsString(args));
        if (args.length >= 1) {
            if (subCommands.containsKey(args[0].toLowerCase())) {
                LocalCommandExecutor sc = subCommands.get(args[0]);
                if (sc.execute(sender, args[0], removeFirstIndex(args))) {
                    return true;
                }

                SettlementMessenger.sendUsage(sender, sc.getUsage());
                return true;
            }
        }
        else if (subCommands.get("info").execute(sender, cmdlbl, removeFirstIndex(args))) {
            return true;
        }
        
        SettlementMessenger.sendUsage(sender, getUsage());
        return true;
    }
    
    /**
     * @return Global /settlement usage help (page 1).
     * */
    public String[] getUsage() {
        
        return usage;
    }

    
    /**
     * Registers the subcommand for a specific executor.
     * 
     * 
     * @param executor Executor to register subcommand for.
     * */
    public void registerSubCommand(LocalCommandExecutor executor) {
        for (String s : executor.getAliases()) {
            subCommands.put(s, executor);
        }
    }


    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        sender.sendMessage("You really shouldn't be seeing this.");
        return false;
    }

    @Override
    public SettlementPermission getPermission() {
        return SettlementPermission.MASTER_PERMISSION;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public LocalCommandExecutor getParent() {
        return this;
    }

    @Override
    public Set<String> getSubCommandAliases() {
        return subCommands.keySet();
    }

    @Override
    public Collection<LocalCommandExecutor> getSubCommands() {
        return subCommands.values();
    }
    
    
}
