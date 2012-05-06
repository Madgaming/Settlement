package net.zetaeta.settlement.commands.settlement.debug;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementData;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;

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
        for (Settlement settlement : Settlement.getSettlements()) {
            for (SettlementPlayer player : settlement.getOnlineMembers()) {
                if (player.getData(settlement) == null) {
                    player.addData(new SettlementData(settlement, (settlement.getOwner() == player ? SettlementRank.OWNER : (settlement.getModeratorNames().contains(player.getName()) ? SettlementRank.MOD : SettlementRank.MEMBER))));
                }
            }
            for (String plNm : settlement.getMemberNames()) {
                SettlementPlayer player;
                if ((player = SettlementPlayer.getSettlementPlayer(plNm)) != null) {
                    if (player.getData(settlement) == null) {
                        player.addData(new SettlementData(settlement, (settlement.getOwner() == player ? SettlementRank.OWNER : (settlement.getModeratorNames().contains(player.getName()) ? SettlementRank.MOD : SettlementRank.MEMBER))));
                    }
                }
            }
        }
        for (SettlementPlayer player : SettlementPlayer.getOnlinePlayers()) {
        }
        return true;
    }
}
