package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Info extends SettlementCommand {

    {
        permission = new SettlementPermission("info", SettlementPermission.USE_BASIC_PERMISSION);
        usage = new String[] {
                "§2 - /settlement info:",
                "§a  Get information about the settlement you have focus on",
        };
        aliases = new String[] {"info"};
    }
    
    public Info(LocalCommand parent) {
        super(parent);
    }
    
    @SuppressWarnings("static-access")
    public boolean execute(CommandSender sender, String alias, String[] args) {
        SettlementPlugin.log.info("Info");
        System.out.println("Info");
        if (alias.equalsIgnoreCase("info")) {
            if (args.length < 1) {
                SettlementMessenger.sendGlobalSettlementInfo(sender);
                return true;
            }
            Settlement settlement = Settlement.getSettlement(SettlementUtil.arrayAsString(args));
            if (settlement == null) {
                SettlementMessenger.sendGlobalSettlementInfo(sender);
                return true;
            }
            if (!SettlementUtil.checkPermission(sender, permission, true)) {
                return true;
            }
            settlement.sendInfoMessage(sender);
            return true;
        }
        System.out.println("preHam");
        System.out.println("args.length = " + args.length);
        SettlementPlugin.log.info("args.length = " + args.length);
        System.out.println("ham");
        if (args.length == 0) {
            System.out.println("argsLength0");
            System.out.println("if");
            SettlementPlugin.log.info("argsLength0");
            if (sender instanceof Player) {
                SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
                System.out.println(sPlayer.getName());
                if (sPlayer.getFocus() == null) {
                    SettlementPlugin.log.info("focusNull");
                    SettlementMessenger.sendGlobalSettlementInfo(sender);
                    return true;
                }
                if (!SettlementUtil.checkPermission(sender, permission, true)) {
                    return true;
                }
                sPlayer.getFocus().sendInfoMessage(sender);
                return true;
            }
            SettlementMessenger.sendGlobalSettlementInfo(sender);
        }
        else {
            SettlementPlugin.log.info("else: args.length = " + args.length);
        }
        SettlementPlugin.log.info("OutsideOfIf");
        Settlement settlement = Settlement.getSettlement(SettlementUtil.arrayAsString(args));
        if (settlement == null) {
            SettlementMessenger.sendGlobalSettlementInfo(sender);
            return true;
        }
        if (!SettlementUtil.checkPermission(sender, permission, true)) {
            return true;
        }
        settlement.sendInfoMessage(sender);
        return true;
    }
    
}
