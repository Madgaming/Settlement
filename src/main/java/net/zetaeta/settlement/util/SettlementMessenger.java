package net.zetaeta.settlement.util;

import org.bukkit.command.CommandSender;

public class SettlementMessenger {

    public static final String SETTLEMENT_MESSAGE_START_LEFT = "§2=========:";

    public static final String SETTLEMENT_MESSAGE_START_RIGHT = "§2:=========";
    
    /**
     * The header String for Settlement messages.
     */
    public static final String SETTLEMENT_MESSAGE_START = SETTLEMENT_MESSAGE_START_LEFT + " §6Settlement " + SETTLEMENT_MESSAGE_START_RIGHT;
    
    /**
     * The footer String for Settlement messages.
     */
    public static final String SETTLEMENT_MESSAGE_END = "§6-----------------------------";
    
    /**
     * The header Strings at the top of all Settlement command help/usage pages.
     */
    public static final String[] SETTLEMENT_USAGE_START = {
        SETTLEMENT_MESSAGE_START,
        "§a-------§dCommand Help§a-------"
    };
    
    /**
     * The footer String at the bottom of all Settlement command help/usage pages.
     * Currently the same as {@link #SETTLEMENT_MESSAGE_END} but exists as a separate constant in case of future change.
     */
    public static final String SETTLEMENT_USAGE_END = SETTLEMENT_MESSAGE_END;
    
    public static final String[] SETTLEMENT_GLOBAL_INFO = {
        "§2  Author: §bZetaeta",
        "§2  Version: §60.1"
    };
    
    /**
     * Sends the specified message to the target in Settlement message form, using {@link #SETTLEMENT_MESSAGE_START} and {@link #SETTLEMENT_USAGE_END}
     * as header and footer.
     * 
     * @param target CommandSender to send the message to.
     * @param message Message to be sent in String... form.
     */
    public static void sendSettlementMessage(CommandSender target, String... message) {
        target.sendMessage(SETTLEMENT_MESSAGE_START);
        target.sendMessage(message);
        target.sendMessage(SETTLEMENT_MESSAGE_END);
    }
    /**
     * Sends the specified message to the target as a command help/usage message, using {@link #SETTLEMENT_USAGE_START} and {@link #SETTLEMENT_USAGE_END}.
     * 
     * @param target CommandSender to send the usage message to.
     * @param usage Usage/help message to send in String... form.
     */
    public static void sendUsage(CommandSender target, String... usage) {
        target.sendMessage(SETTLEMENT_USAGE_START);
        target.sendMessage(usage);
        target.sendMessage(SETTLEMENT_USAGE_END);
    }
    
    /**
     * Sends a "not valid Settlement name" message to the target, if the target specifies a Settlement name but there is no
     * Settlement by that name.
     * 
     * @param target CommandSender to send the message to.
     */
    public static void sendInvalidSettlementMessage(CommandSender target) {
        target.sendMessage(SETTLEMENT_MESSAGE_START);
        target.sendMessage("§c  That is not a valid Settlement's name!");
        target.sendMessage(SETTLEMENT_MESSAGE_END);
    }
    
    /**
     * Sends a message to the target telling them they have no Settlement in their focus.
     * 
     * @param target CommandSender to send the message to.
     */
    public static void sendNoFocusMessage(CommandSender target) {
        target.sendMessage(SETTLEMENT_MESSAGE_START);
        target.sendMessage("§c  You do not have a Settlement in your focus!");
        target.sendMessage(SETTLEMENT_MESSAGE_END);
    }
    
    public static void sendGlobalSettlementInfo(CommandSender target) {
        sendSettlementMessage(target, SETTLEMENT_GLOBAL_INFO);
    }
    
}
