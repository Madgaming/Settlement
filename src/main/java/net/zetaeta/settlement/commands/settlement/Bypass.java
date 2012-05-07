package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bypass extends SettlementCommand {
    
    public Bypass(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - /settlement bypass [-on|-off] [player]:",
                "§a  \u00bbEnables bypass over settlement protection for yourself or the specified player.",
                "§a   If no -on or -off is specified, the bypass will be toggled"
        };
        shortUsage = new String[] {
                "§2 - /settlement bypass",
                "§a  \u00bbControl bypass over settlement protection"
        };
        aliases = new String[] {"bypass"};
        permission = ADMIN_PERMISSION + ".bypass";
    }
    
    @SuppressWarnings("static-access")
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        CommandArguments cArgs = CommandArguments.processArguments(alias, args, new String[] {"on", "off"}, new String[0]);
        args = cArgs.getUnprocessedArgArray();
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        if (args.length > 0 && Bukkit.getPlayer(args[0]) != null) {
            if (!SettlementUtil.checkPermission(sender, permission + ".other", true, true)) {
                SettlementMessenger.sendSettlementMessage(sender, "§c  You are not allowed to set bypass for other players!");
                return true;
            }
            SettlementPlayer target = SettlementPlayer.getSettlementPlayer(Bukkit.getPlayer(args[0]));
            if (cArgs.hasBooleanFlag("on") || (args.length > 1 && args[1].equalsIgnoreCase("on"))) {
                target.setBypass(true);
                SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement protection bypass enabled for §b" + target.getName());
                return true;
            }
            if (cArgs.hasBooleanFlag("off") || (args.length > 1 && args[1].equalsIgnoreCase("off"))) {
                target.setBypass(false);
                SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement protection bypass disabled for §b" + target.getName());
                return true;
            }
            target.setBypass(!target.hasBypass());
            SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(80, "§a  Settlement protection bypass for §b", target.getName(), " §achanged to ", String.valueOf(target.hasBypass()), "!"));
            return true;
        }
        if (cArgs.hasBooleanFlag("on") || (args.length > 0 && args[0].equalsIgnoreCase("on"))) {
            sPlayer.setBypass(true);
            SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement protection bypass enabled!");
            return true;
        }
        if (cArgs.hasBooleanFlag("off") || (args.length > 0 && args[0].equalsIgnoreCase("off"))) {
            sPlayer.setBypass(false);
            SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement protection bypass disabled!");
            return true;
        }
        sPlayer.setBypass(!sPlayer.hasBypass());
        SettlementMessenger.sendSettlementMessage(sender, "§a  Settlement protection bypass changed to " + String.valueOf(sPlayer.hasBypass()) + "!");
        return true;
    }
}
