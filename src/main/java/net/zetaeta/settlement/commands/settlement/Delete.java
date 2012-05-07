package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Delete extends SettlementCommand {
    
    public static Delete scDelete;
    
    {
        permission = OWNER_PERMISSION + ".delete";
        usage = new String[] {
                "§2 - /settlement delete [settlement]:",
                "§a  \u00bbDelete the settlement you specify or have focus over.",
        };
        shortUsage = new String[] {
               "§2 - /settlement delete",
               "§a  \u00bbDelete a settlement."
        };
        aliases = new String[] {"delete", "disband"};
    }
    
    public Delete(LocalCommand parent) {
        super(parent);
        scDelete = this;
    }
    
    
    /**
     * {@inheritDoc}
     * */
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        CommandArguments cArgs = CommandArguments.processArguments(subCommand, args, new String[0], new String[] {"settlement"});
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be run by a player!");
        }
        
        
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, cArgs);
        if (target == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (sPlayer.getRank(target).isEqualOrSuperiorTo(SettlementRank.OWNER) || SettlementUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".delete", false, true)) {
            getDeletionConfirmation(sPlayer, target);
            SettlementMessenger.sendSettlementMessage(sender, new String[] {
                    "§4  Are you sure you want to do this?",
                    "§c  This will delete the Settlement " + target.getName() +" and all its plot/player",
                    "§c  information!",
                    "§a  If you are sure, use §2/settlement confirm §ato confirm the",
                    "§a  deletion"
            });
            return true;
        }
        else {
            target.sendNoRightsMessage(sender);
            return true;
        }
    }

    private static void getDeletionConfirmation(final SettlementPlayer sPlayer, final Settlement settlement) {
        sPlayer.setConfirmable(new Runnable() {
            @SuppressWarnings("static-access")
            @Override
            public void run() {
                String sName = settlement.getName();
                settlement.delete();
                SettlementMessenger.sendSettlementMessage(sPlayer.getPlayer(), SettlementUtil.concatString("§2Settlement ", sName, " has been deleted!"));
            }
        }, 400);
    }
    
}
