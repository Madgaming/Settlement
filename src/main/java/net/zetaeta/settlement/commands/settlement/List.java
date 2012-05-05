package net.zetaeta.settlement.commands.settlement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class List extends SettlementCommand {
    
    
    public List(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - /settlement list [page number]",
                "§a  List all Settlements"
        };
        permission = new SettlementPermission("list", SettlementPermission.USE_BASIC_PERMISSION);
        aliases = new String[] {"list", "all"};
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true)) {
            return true;
        }
        if (args.length == 0) {
            sendList(sender, 0);
            return true;
        }
        int page = 0;
        try {
            page = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage("§Invalid page number!");
            return false;
        }
        sendList(sender, page);
        return true;
    }
    
    @SuppressWarnings("static-access")
    public void sendList(CommandSender sender, int page) {
        log.info(Settlement.getOrderedSettlements().toString());
        log.info(Settlement.settlementsByName.toString());
        ArrayList<Settlement> sList = new ArrayList<Settlement>(Settlement.getOrderedSettlements());
        log.info(sList.toString());
        String[] sentList = new String[11];
        for (int i=0; i<sentList.length; ++i) {
            sentList[i] = "";
        }
        sentList[0] = "§2  Name                           Members online / total members  Plots / max plots";
        for (int i = page * 10, j = 1; i < (page * 10) + 10; ++i, ++j) {
            if (i >= sList.size()) {
                break;
            }
            Settlement set = sList.get(i);
            log.info(j + (set == null ? "null" : set.getName()));
            if (set == null) {
                continue;
            }
            sentList[j] = SettlementUtil.concatString(32 + 12, "§2  ", SettlementMessenger.makeColumns(32, set.getName(), SettlementMessenger.LEFT_ALIGN), set.getPlotCount(), '/', set.getPlotLimit(), "    ", set.getOnlineMemberCount(), '/', set.getMemberCount());
        }
        SettlementMessenger.sendSettlementMessage(sender, sentList);
    }
}
