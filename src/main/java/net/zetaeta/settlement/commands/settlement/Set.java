package net.zetaeta.settlement.commands.settlement;

import java.util.List;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set extends SettlementCommand implements LocalCommandExecutor, SettlementConstants {
    public static final String SET_PERMISSION = OWNER_PERMISSION +  ".set";
    
    public Set(LocalCommand parent) {
        super(parent);
        log.info("Set()");
        List<LocalCommand> registered = registerSubCommands(this);
        usage = new String[] {
                
        };
        aliases = new String[] {"set", "change"};
        permission = new SettlementPermission("set", SettlementPermission.USE_OWNER_PERMISSION);
        log.info("Registered subcommands for Set: " + registered);
    }
    
    public boolean execute(CommandSender sender, String alias, String[] args) {
        log.info("Set");
        if (trySubCommand(sender, alias, args)) {
            log.info("subCommand");
            return true;
        }
        log.info("NoSub");
        return false;
    }
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"slogan", "ham"}, 
            usage = {"§2 - /settlement set slogan <slogan> [-set <settlement name>]", 
                     "§a  "},
            permission = SET_PERMISSION + ".slogan",
            useCommandArguments = true,
            valueFlags = {"settlement", "slogan"})
    public boolean setSlogan(CommandSender sender, CommandArguments args) {
        log.info("setSlogan()");
        if (sender instanceof Player && !args.hasFlagValue("settlement")) { // If a settlement is not specified
            log.info("No -settlement");
            if (args.getUnprocessedArgs().size() == 0) {
                log.info("No unprocessed args!");
                return false;
            }
            SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
            if (sPlayer.getFocus() != null) {
                log.info("sPlayer has focus!");
                Settlement target = sPlayer.getFocus();
                if (sPlayer.getData(target).getRank().isEqualOrSuperiorTo(SettlementRank.MOD) || SettlementUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".set.slogan")) {
                    log.info("HasRights");
                    String slogan = SettlementUtil.arrayAsString(args.getUnprocessedArgArray());
                    target.setSlogan(slogan);
                    target.broadcastSettlementMessage("§b  " + sender.getName() + " §achanged the Settlement's slogan to " + slogan + "!");
                }
                else {
                    target.sendNoRightsMessage(sender);
                }
            }
            else {
                log.info("Focus = null");
                return false;
            }
        }
        String settlementName = null, slogan;
        if (args.hasFlagValue("settlement")) {
            settlementName = args.getFlagValue("settlement");
            if (args.hasFlagValue("slogan")) {
                slogan = args.getFlagValue("slogan");
            }
            else {
                slogan = SettlementUtil.arrayAsString(args.getUnprocessedArgArray());
            }
        }
        else if (args.hasFlagValue("slogan")) {
            slogan = args.getFlagValue("slogan");
            settlementName = SettlementUtil.arrayAsString(args.getUnprocessedArgArray());
        }
        else {
            return false;
        }
        Settlement target = Settlement.getSettlement(settlementName);
        if (target == null) {
            sender.sendMessage("§cThere is no Settlement of that name!");
            return true;
        }
        if (sender.hasPermission(ADMIN_OWNER_PERMISSION + ".set.slogan") || SettlementPlayer.getSettlementPlayer((Player) sender).getData(target).getRank().isEqualOrSuperiorTo(SettlementRank.MOD)) {
            target.setSlogan(slogan);
            target.broadcastSettlementMessage("§b  " + sender.getName() + " §achanged the Settlement's slogan to " + slogan + "!");
        }
        else {
            target.sendNoRightsMessage(sender);
        }
        return true;
    }
    
}
