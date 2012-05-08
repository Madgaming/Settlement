package net.zetaeta.settlement.commands.settlement;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

public class Moderator extends SettlementCommand implements LocalCommandExecutor {
    public static final String MODERATOR_PERMISSION = OWNER_PERMISSION + ".moderator";
    
    public Moderator(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - /settlement moderator add [moderator]:",
                "§a  \u00bbPromote a member of your settlement to moderator.",
                "§2 - /settlement moderator remove [moderator]:",
                "§a  \u00bbRemove one of your moderators from moderator status."
        };
        shortUsage = new String[] {
                "§2 - /settlement moderator",
                "§a  \u00bbManage settlement moderators"
        };
        aliases = new String[] {"moderator", "mod", "assistant"};
        permission = MODERATOR_PERMISSION;
        registerSubCommands(this);
    }
    
    @SuppressWarnings("static-access")
    @Command(usage = {"§2 - /settlement moderator add [moderator]:",
                      "§a  \u00bbPromote a member of your settlement to moderator."},
            shortUsage = {"§2 - /settlement moderator add:",
                          "§a  \u00bbAdd a moderator."},
            aliases = {"add", "promote"},
            permission = MODERATOR_PERMISSION + ".add",
            boolFlags = {"e", "exact", "s", "silent"},
            valueFlags = "settlement",
            playersOnly = true
    )
    public boolean addModerator(CommandSender sender, CommandArguments args) {
        String[] uArgs = args.getUnprocessedArgArray();
        if (uArgs.length == 0) {
            return false;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        SettlementPlayer target;
        if (args.hasBooleanFlag("e") || args.hasBooleanFlag("exact")) {
            target = SettlementPlayer.getSettlementPlayer(Bukkit.getPlayerExact(uArgs[0]));
        }
        else {
            target = SettlementPlayer.getSettlementPlayer(Bukkit.getPlayer(uArgs[0]));
        }
        if (target == null) {
            SettlementMessenger.sendInvalidPlayerMessage(sender);
            return true;
        }
        if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.OWNER) || SettlementUtil.checkPermission(sender, MODERATOR_PERMISSION + ".add", false, true)) {
            if (!settlement.isMember(sPlayer)) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(115, "§c  The player §b", target.getName(), " §cis not a member of the Settlement §6", settlement.getName(), "§c!"));
                return true;
            }
            if (settlement.isModerator(target)) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(120, "§c  The player §b", target.getName(), " §cis already a moderator of the Settlement §6", settlement.getName(), "§c!"));
                return true;
            }
            settlement.addModerator(target);
            if (args.hasBooleanFlag("silent") || args.hasBooleanFlag("s")) {
                SettlementMessenger.sendSettlementMessage(target.getPlayer(), "§b  You §awere demoted from moderator in the settlement §6" + settlement.getName());
                return true;
            }
            settlement.broadcastSettlementMessage(SettlementUtil.concatString("§a  The player §b", target.getName(), " §awas promoted to moderator!"));
            return true;
        }
        settlement.sendNoRightsMessage(sender);
        return true;
    }
    
    @SuppressWarnings("static-access")
    @Command(usage = {"§2 - /settlement moderator remove [moderator]:",
                      "§a  \u00bbRemove one of your moderators from moderator status."},
            shortUsage = {"§2 - /settlement moderator remove:",
                          "§a  \u00bbRemove a moderator."},
            aliases = {"remove", "demote"},
            permission = MODERATOR_PERMISSION + ".add",
            boolFlags = {"e", "exact", "s", "silent"},
            valueFlags = "settlement",
            playersOnly = true
    )
    public boolean removeModerator(CommandSender sender, CommandArguments args) {
        String[] uArgs = args.getUnprocessedArgArray();
        if (uArgs.length == 0) {
            return false;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        SettlementPlayer target;
        if (args.hasBooleanFlag("e") || args.hasBooleanFlag("exact")) {
            target = SettlementPlayer.getSettlementPlayer(Bukkit.getPlayerExact(uArgs[0]));
        }
        else {
            target = SettlementPlayer.getSettlementPlayer(Bukkit.getPlayer(uArgs[0]));
        }
        if (target == null) {
            SettlementMessenger.sendInvalidPlayerMessage(sender);
            return true;
        }
        if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.OWNER) || SettlementUtil.checkPermission(sender, MODERATOR_PERMISSION + ".add", false, true)) {
            if (!settlement.isModerator(target)) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(115, "§c  The player §b", target.getName(), " §cis not a moderator in the settlement §6", settlement.getName(), "§c!"));
                return true;
            }
            settlement.removeModerator(target);
            if (args.hasBooleanFlag("silent") || args.hasBooleanFlag("s")) {
                SettlementMessenger.sendSettlementMessage(target.getPlayer(), "§b  You §awere demoted from moderator in the settlement §6" + settlement.getName());
                return true;
            }
            settlement.broadcastSettlementMessage(SettlementUtil.concatString("§a  The player §b", target.getName(), " §awas demoted from moderator!"));
            return true;
        }
        settlement.sendNoRightsMessage(sender);
        return true;
    }
}
