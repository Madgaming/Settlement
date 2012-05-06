package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.ZPUtil;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementData;
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
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be run by a player.");
            return true;
        }
        
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        
        if (args.length == 0) {
            Settlement target = null;
            if ((target = sPlayer.getFocus()) == null) {
                SettlementMessenger.sendSettlementMessage(sender, new String[] {
                        "§c  You have not set a Settlement in your focus!",
                        "§a  Use §2/settlement focus <settlement name> to set your focus!"
                });
                return true;
            }
            if (SettlementUtil.checkPermission(sender, permission, false, true)) {
                getConfirmation(sPlayer, target);
                SettlementMessenger.sendSettlementMessage(sender, new String[] {
                        "§4  Are you sure you want to do this?",
                        "§c  This will delete the Settlement " + target.getName() +" and all its plot/player",
                        "§c  information!",
                        "§a  If you are sure, use §2/settlement §aconfirm to confirm the",
                        "§a  deletion"
                });
            }
            SettlementData data = sPlayer.getData(sPlayer.getFocus());
            if (!data.getRank().isEqualOrSuperiorTo(SettlementRank.OWNER)) {
                sender.sendMessage("§2You do not have sufficient rights to do this!");
                return true;
            }
            getConfirmation(sPlayer, target);
            SettlementMessenger.sendSettlementMessage(sender, new String[] {
                    "§4  Are you sure you want to do this?",
                    "§c  This will delete your Settlement " + target.getName() +" and all its plot/player information!",
                    "§a  If you are sure, use §2/settlement §aconfirm to confirm the deletion"
            });
            return true;
        }
        else {
            String settlementName = ZPUtil.arrayAsString(args);
            if (SettlementUtil.checkPermission(sender, permission, false, true)) {
                Settlement target = Settlement.getSettlement(settlementName);
                if (target == null) {
                    sender.sendMessage("§cThere is no settlement of that name!");
                    return true;
                }
                getConfirmation(sPlayer, target);
                SettlementMessenger.sendSettlementMessage(sender, new String[] {
                        "§4  Are you sure you want to do this?",
                        "§c  This will delete the Settlement " + target.getName() +" and all its plot/player information!",
                        "§a  If you are sure, use §2/settlement §aconfirm to confirm the deletion"
                });
            }
            else {
                SettlementData data = sPlayer.getData(settlementName);
                if (data == null) {
                    SettlementMessenger.sendInvalidSettlementMessage(sender);
                    return true;
                }
                if (!data.getRank().isEqualOrSuperiorTo(SettlementRank.OWNER)) {
                    data.getSettlement().sendNoRightsMessage(sender);
                    return true;
                }
                
                getConfirmation(sPlayer, data.getSettlement());
                SettlementMessenger.sendSettlementMessage(sender, new String[] {
                        "§4  Are you sure you want to do this?",
                        "§c  This will delete your Settlement " + data.getSettlementName() +" and all its plot/player information!",
                        "§a  If you are sure, use §2/settlement §aconfirm to confirm the deletion"
                });
                return true;
            }
        }
        
        return false;
    }

    private static void getConfirmation(final SettlementPlayer sPlayer, final Settlement settlement) {
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
