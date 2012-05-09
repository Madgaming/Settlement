package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.settlement.util.SettlementMessenger.sendSettlementMessage;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.libraries.util.PermissionUtil;
import net.zetaeta.libraries.util.StringUtil;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandExecutor for the /settlement invite command.
 * 
 * @author Daniel
 *
 */
public class Invite extends SettlementCommand {
    
    public Invite(LocalCommand parent) {
        super(parent);
        permission = OWNER_PERMISSION + ".invite";
        usage = new String[] {
                "§2 - /settlement invite <player>",
                "§a  \u00bbInvite <player> to the settlement"
        };
        shortUsage = new String[] {
                "§2 - /settlement invite",
                "§a  \u00bbInvite a player to a settlement."
        };
        aliases = new String[] {"invite", "add"};
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        if (!PermissionUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        CommandArguments cArgs = CommandArguments.processArguments(alias, args, new String[] {"exact", "e"}, new String[] {"settlement"}, sender);
        if (cArgs == null)
            return true;
        SettlementPlayer sPlayer = null;
        String[] newArgs = cArgs.getUnprocessedArgArray();
        if (newArgs.length < 1) {
            SettlementMessenger.sendUsage(sender, usage);
            return true;
        }
        if (sender instanceof Player) {
            sPlayer = server.getSettlementPlayer((Player) sender);
        }
        else {
            sender.sendMessage("§cThis command can only be run by a player!");
            return true;
        }
        Settlement invitedTo = SettlementUtil.getFocusedOrStated(sPlayer, cArgs);
        if (invitedTo != null) {
            if (!sPlayer.getRank(invitedTo).isEqualOrSuperiorTo(Rank.MODERATOR) && !PermissionUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".invite", true, true)) {
                invitedTo.sendNoRightsMessage(sender);
                return true;
            }
            if (cArgs.hasBooleanFlag("e") || cArgs.hasBooleanFlag("exact")) { // exact name
                
                invitedTo.addInvitation(newArgs[0]);
                sendSettlementMessage(sender, StringUtil.concatString("§b  You §ahave invited ", newArgs[0], " to your Settlement, ", invitedTo.getName()));
                if (Bukkit.getPlayerExact(newArgs[0]) != null) {
                    sendSettlementMessage(Bukkit.getPlayerExact(newArgs[0]), StringUtil.concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                }
                return true;
            }
            if (newArgs.length == 1) {
                if (Bukkit.getPlayer(newArgs[0]) != null) {
                    Player invitee = Bukkit.getPlayer(newArgs[0]);
                    invitedTo.addInvitation(invitee.getName());
                    sendSettlementMessage(sender, StringUtil.concatString("§b  You §2have invited §b", invitee.getName(), " §2to your Settlement, §6", invitedTo.getName(), "§2!"));
                    sendSettlementMessage(invitee, StringUtil.concatString("§b  ", sender.getName(), " §2has invited you to the Settlement §6", invitedTo.getName(), "§2!"));
                    return true;
                }
                sender.sendMessage("§cNot an online player!");
                return true;
            }
            
        }
        return false;
    }
}
