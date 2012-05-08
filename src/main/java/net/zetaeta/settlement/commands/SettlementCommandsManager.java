package net.zetaeta.settlement.commands;

import static net.zetaeta.libraries.ZPUtil.removeFirstIndex;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.DynamicCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.commands.settlement.Bypass;
import net.zetaeta.settlement.commands.settlement.Claims;
import net.zetaeta.settlement.commands.settlement.Confirm;
import net.zetaeta.settlement.commands.settlement.Focus;
import net.zetaeta.settlement.commands.settlement.Help;
import net.zetaeta.settlement.commands.settlement.Info;
import net.zetaeta.settlement.commands.settlement.Invite;
import net.zetaeta.settlement.commands.settlement.Join;
import net.zetaeta.settlement.commands.settlement.Leave;
import net.zetaeta.settlement.commands.settlement.List;
import net.zetaeta.settlement.commands.settlement.Moderator;
import net.zetaeta.settlement.commands.settlement.OwnerCommands;
import net.zetaeta.settlement.commands.settlement.Spawn;
import net.zetaeta.settlement.commands.settlement.Usage;
import net.zetaeta.settlement.commands.settlement.debug.Debug;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SettlementCommandsManager extends DynamicCommandExecutor implements LocalCommand, SettlementConstants {
    
    private final String[] aliases = {};
    private Map<String, LocalCommand> subCommands = new HashMap<String, LocalCommand>();
    private String[] usage;
    private String[] shortUsage;
    public static SettlementCommandsManager settlementCommandsManager;
    
    public SettlementCommandsManager() {
        settlementCommandsManager = this;
        usage = new String[] {"settlement"};
        
        registerSubCommand(new Bypass(this));
        registerSubCommands(new Claims());
        registerSubCommand(new Confirm(this));
//        registerSubCommand(new Create(this));
//        registerSubCommand(new Delete(this));
        registerSubCommand(new Focus(this));
        registerSubCommand(new Help(this));
        registerSubCommand(new Info(this));
        registerSubCommand(new Invite(this));
        registerSubCommand(new Join(this));
        registerSubCommand(new Leave(this));
        registerSubCommand(new List(this));
        registerSubCommand(new Moderator(this));
        registerSubCommands(new OwnerCommands());
        registerSubCommand(new net.zetaeta.settlement.commands.settlement.Set(this));
        registerSubCommands(new Spawn());
        registerSubCommand(new Usage(this));
        
        registerSubCommand(new Debug(this));
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
        if (args.length >= 1) {
            if (subCommands.containsKey(args[0].toLowerCase())) {
                LocalCommand sc = subCommands.get(args[0]);
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

    public String[] getShortUsage() {
        return shortUsage;
    }
    
    /**
     * Registers the subcommand for a specific executor.
     * 
     * 
     * @param executor Executor to register subcommand for.
     * */
    public LocalCommand registerSubCommand(LocalCommand executor) {
        for (String s : executor.getAliases()) {
            subCommands.put(s, executor);
        }
        return executor;
    }
    
    public java.util.List<LocalCommand> registerSubCommands(LocalCommandExecutor executor) {
        Class<? extends LocalCommandExecutor> executorClass = executor.getClass();
        java.util.List<LocalCommand> registered = new ArrayList<LocalCommand>(executorClass.getMethods().length);
        for (Method m : executorClass.getDeclaredMethods()) {
            for (Annotation a : m.getAnnotations()) {
            }
            if (m.isAnnotationPresent(net.zetaeta.libraries.commands.local.Command.class)) {
                registered.add(registerSubCommand(new SettlementExecutorWrapper(this, executor, m)));
            }
            else {
                for (Annotation annotation : m.getAnnotations()) {
                }
            }
        }
        return registered;
    }

    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        sender.sendMessage("You really shouldn't be seeing this.");
        return false;
    }

    @Override
    public String getPermission() {
        return SettlementCommand.MASTER_PERMISSION;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public LocalCommand getParent() {
        return this;
    }

    @Override
    public Set<String> getSubCommandAliases() {
        return subCommands.keySet();
    }

    @Override
    public Collection<LocalCommand> getSubCommands() {
        return new HashSet<LocalCommand>(subCommands.values());
    }

    @Override
    public Collection<LocalCommand> getOrderedSubCommands() {
        return new TreeSet<LocalCommand>(subCommands.values());
    }

    @Override
    public void sendUsage(CommandSender target) {
        SettlementMessenger.sendUsage(target, getUsage());
    }
    
    public LocalCommand getSubCommand(String alias) {
        String[] aliases = alias.trim().split(" ");
        if (aliases.length == 1) {
            return subCommands.get(alias);
        }
        else {
            return subCommands.get(aliases[0]) == null ? null : subCommands.get(aliases[0]).getSubCommand(ZPUtil.removeFirstIndex(aliases));
        }
    }
    
    public LocalCommand getSubCommand(String[] aliases) {
        if (aliases.length == 1) {
            return subCommands.get(aliases[0]);
        }
        else {
            return subCommands.get(aliases[0]) == null ? null : subCommands.get(aliases[0]).getSubCommand(ZPUtil.removeFirstIndex(aliases));
        }
    }
    
    public String toString() {
        return "/settlement";
    }
}
