package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.concatString;
import static net.zetaeta.settlement.util.SettlementMessenger.sendSettlementMessage;
import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
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
    private String[] usage;
    
    public static Invite invite;
    
    {
        permission = new SettlementPermission("invite", SettlementPermission.USE_OWNER_PERMISSION);
        usage = new String[] {
                "§2 - /settlement invite <player>"
        };
        aliases = new String[] {"invite", "add"};
    }
    
    public Invite(LocalCommand parent) {
        super(parent);
        invite = this;
    }
    
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true)) {
            return true;
        }
        CommandArguments cArgs = CommandArguments.processArguments(args, new String[] {"exact", "e"}, new String[0], sender);
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
        if (sPlayer != null && sPlayer.getFocus() != null) {
            Settlement invitedTo = sPlayer.getFocus();
            if (!sPlayer.getRank(invitedTo).isEqualOrSuperiorTo(SettlementRank.MOD) && !SettlementUtil.checkPermission(sender, permission.getAdminPermission(), true)) {
                sendSettlementMessage(sender, "§4You do not have the required rights in Settlement " + invitedTo.getName());
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
                    sendSettlementMessage(sender, concatString("§2You have invited ", invitee.getName(), " to your Settlement, ", invitedTo.getName()));
                    sendSettlementMessage(Bukkit.getPlayerExact(newArgs[1]), concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                    return true;
                }
                sender.sendMessage("§cNot an online player!");
                return true;
            }
            
        }
        return false;
    }
}
