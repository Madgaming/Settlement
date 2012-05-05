package net.zetaeta.settlement.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.ExecutorWrapper;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettlementExecutorWrapper extends ExecutorWrapper implements SettlementConstants {
    public SettlementExecutorWrapper(LocalCommand parent, LocalCommandExecutor executor, Method executorMethod) {
        super(parent, executor, executorMethod);
        log.info("SettlementExecutorWrapper()");
    }
    
    @Override
    public void sendUsage(CommandSender target) {
        SettlementMessenger.sendUsage(target, getUsage());
    }
    
    
    @Override
    public LocalPermission createPermission(String valueString) {
//        try {
            if (((SettlementPermission) parent.getPermission()).getChild(valueString) != null) {
                return ((SettlementPermission) parent).getChild(valueString);
            }
            else {
                return new SettlementPermission(valueString, (SettlementPermission) parent.getPermission());
            }
/*            if (valueString == "") {
                return null;
            }
            String[] nodes = valueString.split("\\.");
            String[] actual;
            if (nodes.length < 1) {
                throw new IllegalArgumentException("Permission String too short");
            }
            int i = nodes.length;
            if (nodes[0].equalsIgnoreCase("settlement")) {
                if (i < 2)
                nodes = Arrays.copyOfRange(nodes, 1, nodes.length);
            }
            SettlementPermission parent;
            if (nodes[0].equalsIgnoreCase("use")) {
                if (i == 1) {
                    parent = SettlementPermission.USE_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 1, i);
                }
                else if (nodes[1].equalsIgnoreCase("basic")) {
                    parent = SettlementPermission.USE_BASIC_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 2, i);
                }
                else if (nodes[1].equalsIgnoreCase("owner")) {
                    parent = SettlementPermission.USE_OWNER_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 2, i);
                }
                else {
                    parent = SettlementPermission.USE_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 1, i);
                }
            }
            else if (nodes[0].equalsIgnoreCase("admin")) {
                if (i == 1) {
                    parent = SettlementPermission.ADMIN_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 1, i);
                }
                else if (nodes[1].equalsIgnoreCase("basic")) {
                    parent = SettlementPermission.ADMIN_BASIC_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 2, i);
                }
                else if (nodes[1].equalsIgnoreCase("owner")) {
                    parent = SettlementPermission.ADMIN_OWNER_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 2, i);
                }
                else {
                    parent = SettlementPermission.ADMIN_PERMISSION;
                    actual = Arrays.copyOfRange(nodes, 1, i);
                }
            }
            else {
                parent = SettlementPermission.MASTER_PERMISSION;
                actual = nodes;
            }
            if (parent.getChild(actual[0]) != null) {
                
            }
            SettlementPermission currentIt = new SettlementPermission(actual[0], parent);
            for (int j=1; i<actual.length; ++i) {
                currentIt = new SettlementPermission(actual[j], currentIt);
            }
            return currentIt;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Permission String too short!", e);
        }*/
    }
}
