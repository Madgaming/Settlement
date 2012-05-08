package net.zetaeta.settlement;

import java.util.logging.Logger;

import net.zetaeta.settlement.object.SettlementServer;

public interface SettlementConstants {
    public static final Logger log = SettlementPlugin.log;
    public static final SettlementPlugin plugin = SettlementPlugin.plugin;
    public static final SettlementServer server = plugin.getSettlementServer();
}
