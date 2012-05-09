package net.zetaeta.settlement.commands.settlement.debug;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementData;
import net.zetaeta.settlement.object.SettlementPlayer;

import org.bukkit.command.CommandSender;

public class ReloadSettlementData extends SettlementCommand {
    public ReloadSettlementData(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "reloadsettlementdata"
        };
        aliases = new String[] {"reloaddata", "rsd", "rd", "reloadsettlementdata"};
        permission = Debug.DEBUG_PERMISSION + "reloadsettlementdata";
    }
    
    public boolean execute(CommandSender sender, String alias, String[] args) {
        sender.sendMessage("Reloading...");
        for (Settlement settlement : server.getSettlements()) {
            for (SettlementPlayer player : settlement.getOnlineMembers()) {
                player.setRank(settlement, (settlement.getOwner() == player ? Rank.OWNER : (settlement.getModeratorNames().contains(player.getName()) ? Rank.MODERATOR : Rank.MEMBER)));
            }
            for (String plNm : settlement.getMemberNames()) {
                SettlementPlayer player;
                if ((player = server.getSettlementPlayer(plNm)) != null) {
                    player.setRank(settlement, (settlement.getOwner() == player ? Rank.OWNER : (settlement.getModeratorNames().contains(player.getName()) ? Rank.MODERATOR : Rank.MEMBER)));
                }
            }
        }
        return true;
    }
}
