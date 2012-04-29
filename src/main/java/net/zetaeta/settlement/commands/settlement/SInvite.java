package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.concatString;
import static net.zetaeta.settlement.util.SettlementMessenger.sendSettlementMessage;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
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
public class SInvite extends SettlementCommand {
    private String[] usage;
    
    public static SInvite sInvite;
    
    {
        permission = new SettlementPermission("invite", SettlementPermission.USE_OWNER_PERMISSION);
        usage = new String[] {
                "§2 - /settlement invite <player>"
        };
        aliases = new String[] {"invite", "add"};
    }
    
    public SInvite(LocalCommandExecutor parent) {
        super(parent);
        sInvite = this;
        parent.registerSubCommand(this);
    }
    
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true)) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(usage);
            return true;
        }
        SettlementPlayer sPlayer = null;
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
            if (args[0].equalsIgnoreCase("-e")) { // exact name
                if (args.length < 2) {
                    return false;
                }
                invitedTo.addInvitation(args[1]);
                sendSettlementMessage(sender, concatString("§2You have invited ", args[1], " to your Settlement, ", invitedTo.getName()));
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    sendSettlementMessage(Bukkit.getPlayerExact(args[1]), concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                }
                return true;
            }
            if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player invitee = Bukkit.getPlayer(args[0]);
                    invitedTo.addInvitation(invitee.getName());
                    sendSettlementMessage(sender, concatString("§2You have invited ", invitee.getName(), " to your Settlement, ", invitedTo.getName()));
                    sendSettlementMessage(Bukkit.getPlayerExact(args[1]), concatString("§2", sender.getName(), " has invited you to the Settlement ", invitedTo.getName(), "!"));
                    return true;
                }
                sender.sendMessage("§cNot an online player!");
                return true;
            }
            
        }
        return false;
    }
}
