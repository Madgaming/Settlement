package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.concatString;
import static net.zetaeta.settlement.util.SettlementMessenger.sendSettlementMessage;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
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
    
    public static Invite invite;
    
    {
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
    
    public Invite(LocalCommand parent) {
        super(parent);
        invite = this;
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
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
            sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        }
        else {
            sender.sendMessage("§cThis command can only be run by a player!");
            return true;
        }
        Settlement invitedTo = SettlementUtil.getFocusedOrStated(sPlayer, cArgs);
        if (invitedTo != null) {
            if (!sPlayer.getRank(invitedTo).isEqualOrSuperiorTo(SettlementRank.MOD) && !SettlementUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".invite", true, true)) {
                invitedTo.sendNoRightsMessage(sender);
                return true;
            }
            if (cArgs.hasBooleanFlag("e") || cArgs.hasBooleanFlag("exact")) { // exact name
                
                invitedTo.addInvitation(newArgs[0]);
                sendSettlementMessage(sender, concatString("§2You have invited ", newArgs[0], " to your Settlement, ", invitedTo.getName()));
                if (Bukkit.getPlayerExact(newArgs[0]) != null) {
                    sendSettlementMessage(Bukkit.getPlayerExact(newArgs[0]), concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                }
                return true;
            }
            if (newArgs.length == 1) {
                if (Bukkit.getPlayer(newArgs[0]) != null) {
                    Player invitee = Bukkit.getPlayer(newArgs[0]);
                    invitedTo.addInvitation(invitee.getName());
                    sendSettlementMessage(sender, concatString("§bYou §2have invited §b", invitee.getName(), " §2to your Settlement, §6", invitedTo.getName(), "§2!"));
                    sendSettlementMessage(invitee, concatString("§b", sender.getName(), " §2has invited you to the Settlement §6", invitedTo.getName(), "§2!"));
                    return true;
                }
                sender.sendMessage("§cNot an online player!");
                return true;
            }
            
        }
        return false;
    }
}
