package net.zetaeta.settlement.commands.settlement;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

public class OwnerCommands implements LocalCommandExecutor {
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"create", "new"},
            usage = {"§2 - /settlement create <settlement name>",
                     "§a  \u00bbCreate a settlement with the given name, with you as owner.",},
            shortUsage = {"§2 - /settlement create",
                          "§a  \u00bbCreate a settlement"},
            permission = SettlementCommand.OWNER_PERMISSION + ".create",
            useCommandArguments = true,
            playersOnly = true)
    public boolean create(CommandSender sender, CommandArguments args) {
        String[] rawArgs = args.getUnprocessedArgArray();
        if (rawArgs.length < 1) {
            return false;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        String setName = SettlementUtil.arrayAsString(rawArgs);
        if (Settlement.getSettlement(setName) != null) {
            SettlementMessenger.sendSettlementMessage(sender, "§cA settlement with the name §6" + setName + " §calready exists!");
            return true;
        }
        if (setName.length() > 32) {
            SettlementMessenger.sendSettlementMessage(sender, "§cThat name is too long!");
            return true;
        }
        Settlement settlement = new Settlement(sPlayer, SettlementUtil.arrayAsString(rawArgs), Settlement.getNewUID());
        sPlayer.setRank(settlement, SettlementRank.OWNER);
        settlement.broadcastSettlementMessage("§a  Settlement Created!");
        return true;
    }
    
    @Command(aliases = {"delete", "disband"},
            usage = {"§2 - /settlement delete [settlement]:",
                      "§a  \u00bbDelete the settlement you specify or have focus over."},
            shortUsage = {"§2 - /settlement delete",
                          "§a  \u00bbDelete a settlement."},
            permission = SettlementCommand.OWNER_PERMISSION + ".delete",
            useCommandArguments = true,
            playersOnly = true)
    public boolean delete(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (target == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (sPlayer.getRank(target).isEqualOrSuperiorTo(SettlementRank.OWNER) || SettlementUtil.checkPermission(sender, SettlementCommand.ADMIN_OWNER_PERMISSION + ".delete", false, true)) {
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
