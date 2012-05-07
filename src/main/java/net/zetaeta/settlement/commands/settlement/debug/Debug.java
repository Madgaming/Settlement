package net.zetaeta.settlement.commands.settlement.debug;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class Debug extends SettlementCommand {
    public static final String DEBUG_PERMISSION = ADMIN_BASIC_PERMISSION + ".debug";
    private Object cache;
    
    public Debug(LocalCommand parent) {
        super(parent);
        permission = DEBUG_PERMISSION;
        usage = new String[] {
                "§2 - /settlement debug",
                "§a  Debug stuff"
        };
        shortUsage = new String[] {
                "", ""
        };
        aliases = new String[] {"debug"};
        registerSubCommand(new ReloadSettlementData(this));
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission) && !sender.isOp()) {
            return false;
        }
        String[] todo = args[0].split("::");
        if (todo[0].equals(">")) {
            sender.sendMessage(String.valueOf(cache));
            return true;
        }
        if (todo[0].equals("~")) {
            Method targm = null;
            for (Method m : cache.getClass().getDeclaredMethods()) {
                if (m.getName().equals(todo[1])) {
                    targm = m;
                }
            }
            if (targm == null) {
                sender.sendMessage("null");
                return false;
            }
            try {
                cache = targm.invoke(cache, ZPUtil.removeFirstIndex(args));
                sender.sendMessage("Result of method " + targm.getName() + " of the cache, which is of class " + cache.getClass().getName() + " pushed onto cache!");
                return true;
            } catch (IllegalAccessException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                String s = "";
                for (Class<?> c : targm.getParameterTypes()) {
                    s += c.getName();
                }
                sender.sendMessage("Should have been: " + s);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            String[] fieldArr = todo[0].split("#");
            try {
                Class<?> clazz = Class.forName(fieldArr[0]);
                Field field = clazz.getDeclaredField(fieldArr[1]);
                field.setAccessible(true);
                if (todo[1].equals("~")) {
                    cache = field.get(null);
                    sender.sendMessage("Field " + field.getName() + " of class " + field.getType().getName() + " pushed onto cache!");
                    return true;
                }
                else {
                    Method targm = null;
                    for (Method m : field.getType().getDeclaredMethods()) {
                        if (m.getName().equals(todo[1])) {
                            targm = m;
                        }
                    }
                    if (targm == null) {
                        sender.sendMessage("null");
                        return false;
                    }
                    try {
                        cache = targm.invoke(cache, args);
                        sender.sendMessage("Result of method " + targm.getName() + " of field " + field.getName() + " of class " + field.getType().getName() + " pushed onto cache!");
                    } catch (IllegalAccessException e) {
                        sender.sendMessage(e.getClass().getName() + e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(e.getClass().getName() + e.getMessage());
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        sender.sendMessage(e.getClass().getName() + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            } catch (SecurityException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                sender.sendMessage(e.getClass().getName() + e.getMessage());
                e.printStackTrace();
            }
            
        }
        return false;
    }
}
