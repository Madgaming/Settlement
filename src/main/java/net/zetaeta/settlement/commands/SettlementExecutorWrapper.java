package net.zetaeta.settlement.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.ExecutorWrapper;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.util.PermissionUtil;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementThreadManager;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettlementExecutorWrapper extends ExecutorWrapper implements SettlementConstants, Comparable<LocalCommand> {
    public SettlementExecutorWrapper(LocalCommand parent, LocalCommandExecutor executor, Method executorMethod) {
        super(parent, executor, executorMethod);
    }
    
    @Override
    public void sendUsage(CommandSender target) {
        SettlementMessenger.sendUsage(target, getUsage());
    }

    @Override
    public int compareTo(LocalCommand other) {
        return aliases[0].compareTo(other.getAliases()[0]);
    }
    
    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        if (annotation.checkPermissions() && !PermissionUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        if (annotation.playersOnly() && !(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be run by a player!");
            return true;
        }
        if (executorMethod.isAnnotationPresent(Multithreaded.class)) {
            SettlementThreadManager.submitAsyncTask(new Runnable() {
                @Override
                public void run() {
                    boolean success;
                    try {
                        if (useCmdArgs) {
                            success = (Boolean) executorMethod.invoke(executor, sender, CommandArguments.processArguments(alias, args, annotation.boolFlags(), annotation.valueFlags()));
                        }
                        else {
                            success = (Boolean) executorMethod.invoke(executor, sender, alias, args);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        sender.sendMessage("§cAn internal error occurred while executing this command.");
                        return;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        sender.sendMessage("§cAn internal error occurred while executing this command.");
                        return;
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                        sender.sendMessage("§cAn internal error occurred while executing this command.");
                        return;
                    }
                    if (!success) {
                        sender.sendMessage(usage);
                    }
                }
            });
            return true;
        }
        boolean success;
        try {
            if (useCmdArgs) {
                success = (Boolean) executorMethod.invoke(executor, sender, CommandArguments.processArguments(alias, args, annotation.boolFlags(), annotation.valueFlags()));
            }
            else {
                success = (Boolean) executorMethod.invoke(executor, sender, alias, args);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            sender.sendMessage("§cAn internal error occurred while executing this command.");
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sender.sendMessage("§cAn internal error occurred while executing this command.");
            return true;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            e.getCause().printStackTrace();
            sender.sendMessage("§cAn internal error occurred while executing this command.");
            return true;
        }
        return success;
    }
}
