package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.Command;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements LocalCommandExecutor {
    
    @Command(usage = {"§2 - /settlement spawn [settlement]:",
                      "§a  \u00bbTeleport to the settlement's spawn"},
            shortUsage = {"§2 - /settlement spawn:",
                          "§a  \u00bbTeleport to a settlement's spawn."},
            aliases = {"spawn", "home"},
            valueFlags = {"settlement"},
            permission = SettlementCommand.BASIC_PERMISSION + ".spawn",
            playersOnly = true
    )
    public boolean spawn(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer(player);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        player.teleport(settlement.getSpawn());
        SettlementMessenger.sendSettlementMessage(sender, "§a  Teleported to the spawn of §6" + settlement.getName());
        return true;
    }
    @SuppressWarnings("static-access")
    @Command(usage = {"§2 - /settlement setspawn [settlement]:",
                      "§a  \u00bbSet the settlement's spawn to your current location"},
            shortUsage = {"§2 - /settlement setspawn:",
                    "§a  \u00bbSet a settlement's spawn."},
            aliases = {"setspawn", "sethome"},
            valueFlags = {"settlement"},
            permission = Set.SET_PERMISSION + ".spawn",
            playersOnly = true
    )
    public boolean setSpawn(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer(player);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (SettlementUtil.checkPermission(sender, Set.SET_ADMIN_PERMISSION + ".spawn", false, true) || sPlayer.getRank(settlement).isEqualOrSuperiorTo(SettlementRank.MODERATOR)) {
            if (!settlement.equals(SettlementUtil.getOwner(player.getLocation().getChunk()))) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(80, "§c  The settlement §6", settlement.getName(), " §cdoes not own this plot!"));
                return true;
            }
            Location loc = player.getLocation();
            settlement.setSpawn(loc);
            SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(125, "§b  You §aset the spawn of §6", settlement.getName(), "§a to x=", loc.getBlockX(), ", y=", loc.getBlockY(), ", z=", loc.getBlockZ(), " in world ", loc.getWorld().getName(), "!"));
            return true;
        }
        settlement.sendNoRightsMessage(sender);
        return true;
    }
}
