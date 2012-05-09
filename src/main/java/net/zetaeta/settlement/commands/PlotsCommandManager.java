package net.zetaeta.settlement.commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.zetaeta.libraries.Util;
import net.zetaeta.libraries.commands.DynamicCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.command.CommandSender;

public class PlotsCommandManager extends DynamicCommandExecutor implements LocalCommand, SettlementConstants {
    private Map<String, LocalCommand> subCommands = new HashMap<String, LocalCommand>();
    private String[] localUsage;
    private String[] aliases;
    
    public PlotsCommandManager() {
        
    }
    
    @Override
    public LocalCommand getParent() {
        return this;
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
    public Set<String> getSubCommandAliases() {
        return subCommands.keySet();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return localUsage;
    }

    @Override
    public String[] getShortUsage() {
        return localUsage;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public LocalCommand registerSubCommand(LocalCommand executor) {
        for (String s : executor.getAliases()) {
            subCommands.put(s, executor);
        }
        return executor;
    }

    @Override
    public List<LocalCommand> registerSubCommands(LocalCommandExecutor executor) {
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
    public boolean execute(CommandSender sender, String alias, String[] args) {
        sender.sendMessage("You really shouldn't be seeing this.");
        return false;
    }

    @Override
    public void sendUsage(CommandSender target) {
        SettlementMessenger.sendUsage(target, getUsage());
    }

    @Override
    public LocalCommand getSubCommand(String alias) {
        String[] aliases = alias.trim().split(" ");
        if (aliases.length == 1) {
            return subCommands.get(alias);
        }
        else {
            return subCommands.get(aliases[0]) == null ? null : subCommands.get(aliases[0]).getSubCommand(Util.removeFirstIndex(aliases));
        }
    }

    @Override
    public LocalCommand getSubCommand(String[] aliases) {
        if (aliases.length == 1) {
            return subCommands.get(aliases[0]);
        }
        else {
            return subCommands.get(aliases[0]) == null ? null : subCommands.get(aliases[0]).getSubCommand(Util.removeFirstIndex(aliases));
        }
    }
    
    public String toString() {
        return "/plot";
    }
}
