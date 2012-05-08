package net.zetaeta.settlement.commands.settlement;

import java.util.ArrayList;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.SettlementThreadManager;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;

public class List extends SettlementCommand {
    
    
    public List(LocalCommand parent) {
        super(parent);
        usage = new String[] {
                "§2 - /settlement list [page number]",
                "§a  \u00bbList all Settlements"
        };
        shortUsage = new String[] {
                "§2 - /settlement list",
                "§a  \u00bbList all Settlements"
        };
        permission = BASIC_PERMISSION + ".list";
        aliases = new String[] {"list", "all"};
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(final CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        if (args.length == 0) {
            SettlementThreadManager.submitAsyncTask(new Runnable() {
                public void run() {
                    sendList(sender, 0);
                }
            });
            return true;
        }
        final int page;
        try {
            page = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage("§Invalid page number!");
            return false;
        }
        SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                sendList(sender, page);
            }
        });
        return true;
    }
    
    @SuppressWarnings("static-access")
    public void sendList(CommandSender sender, int page) {
        ArrayList<Settlement> sList = new ArrayList<Settlement>(server.getOrderedSettlements());
        ArrayList<String> sentList = new ArrayList<String>(11);
        sentList.add("§2  Name            |             Members |  Plots");
        for (int i = page * 10, j = 1; i < (page * 10) + 10; ++i, ++j) {
            if (i >= sList.size()) {
                break;
            }
            Settlement set = sList.get(i);
            if (set == null) {
                continue;
            }
            sentList.add(SettlementUtil.concatString(32 + 12, "§2  ", SettlementMessenger.makeColumns(32, set.getName(), SettlementMessenger.LEFT_ALIGN), set.getPlotCount(), '/', set.getPlotLimit(), "      ", set.getOnlineMemberCount(), '/', set.getMemberCount()));
        }
        SettlementMessenger.sendSettlementMessage(sender, sentList.toArray(new String[sentList.size()]));
    }
}
