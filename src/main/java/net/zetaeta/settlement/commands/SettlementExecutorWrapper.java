package net.zetaeta.settlement.commands;

import java.lang.reflect.Method;

import net.zetaeta.libraries.commands.local.ExecutorWrapper;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.command.CommandSender;

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
    
    
}
