package net.zetaeta.settlement.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.AbstractLocalCommandExecutor;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.ExecutorWrapper;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class SettlementCommand extends AbstractLocalCommandExecutor implements SettlementConstants {
    public static final String MASTER_PERMISSION = "settlement";
    public static final String BASIC_PERMISSION = MASTER_PERMISSION + ".basic";
    public static final String OWNER_PERMISSION = MASTER_PERMISSION + ".owner";
    public static final String ADMIN_PERMISSION = MASTER_PERMISSION + ".admin";
    public static final String ADMIN_BASIC_PERMISSION = ADMIN_PERMISSION + ".basic";
    public static final String ADMIN_OWNER_PERMISSION = ADMIN_PERMISSION + ".owner";
    
    protected SettlementPermission permission;
    
    public SettlementCommand(LocalCommand parent) {
        super(parent);
    }
    
    public SettlementCommand(LocalCommand parent, LocalPermission permission, String[] usage, String[] aliases) {
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

    @Override
    public void sendUsage(CommandSender target) {
        SettlementMessenger.sendUsage(target, getUsage());
    }
    
    
    public List<LocalCommand> registerSubCommands(LocalCommandExecutor commandsExecutor) {
        Class<? extends LocalCommandExecutor> executorClass = commandsExecutor.getClass();
        List<LocalCommand> registered = new ArrayList<LocalCommand>(executorClass.getMethods().length);
        for (Method m : executorClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                registered.add(registerSubCommand(new SettlementExecutorWrapper(this, commandsExecutor, m)));
            }
        }
        return registered;
    }
}
