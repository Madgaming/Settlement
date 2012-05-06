package net.zetaeta.settlement.commands.settlement;

import java.util.ArrayList;
import java.util.Iterator;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class Help extends SettlementCommand {

    public Help(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - " + parent.toString() + " help [page]:",
                "§a  \u00bbGet a list of settlement commands with usage information.",
                "§a  \u00bbFor more specific info on a command, use /settlement usage <command>"
        };
        shortUsage = new String[] {
                "§2 - " + parent.toString() +" help",
                "§a  \u00bbShow a list of all commands with basic help info"
        };
        permission = BASIC_PERMISSION + ".help";
        aliases = new String[] {"help"};
    }
    
    @SuppressWarnings("static-access")
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender, 0);
            return true;
        }
        int page;
        try {
            page = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            return false;
        }
        sendHelp(sender, (page - 1) * 8);
        return true;
    }
    
    public void sendHelp(CommandSender target, int page) {
        int count = 0;
        ArrayList<String> usage = new ArrayList<String>();
        for (Iterator<LocalCommand> subCommands = parent.getOrderedSubCommands().iterator(); subCommands.hasNext() && count < page + 8;) {
            if (count < page) {
                log.info("Bypassing: " + subCommands.next().getAliases()[0]);
                log.info("Count: " + count);
                log.info("Page: " + page);
                ++count;
                continue;
            }
            LocalCommand cmd = subCommands.next();
            log.info(cmd.getAliases()[0]);
            usage.add(cmd.getShortUsage()[0]);
            usage.add(cmd.getShortUsage()[1]);
            ++count;
        }
        if (usage.size() == 0) {
            return;
        }
        SettlementMessenger.sendUsage(target, usage.toArray(new String[usage.size()]));
    }
}
