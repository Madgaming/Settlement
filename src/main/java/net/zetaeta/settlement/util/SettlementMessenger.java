package net.zetaeta.settlement.util;

import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.Settlement;

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
    
    public static final int LEFT_ALIGN = 0x01;
    
    public static final int RIGHT_ALIGN = 0x10;
    
    public static final int CENTRE_ALIGN = 0x11;
    
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
    
    public static void sendWildernessMessage(CommandSender target) {
        target.sendMessage("  ".concat(ConfigurationConstants.wildernessMessage));
    }
    
    @SuppressWarnings("static-access")
    public static void sendPlotChangeMessage(CommandSender target, Settlement change) {
        target.sendMessage(SettlementUtil.concatString(60, "  §b~ §6", change.getName(), " - §a", change.getSlogan().trim()));
    }
    
    public static String makeColumns(int columns, String base, int style) {
        StringBuilder sb = new StringBuilder(columns);
        if (base.length() > columns) {
            base = base.substring(0, columns);
        }
        switch (style) {
        case LEFT_ALIGN :
            sb.append(base);
            for (int i=0; i < columns - base.length(); ++i) {
                sb.append(' ');
            }
            break;
        case RIGHT_ALIGN :
            for (int i=0; i < columns - base.length(); ++i) {
                sb.append(' ');
            }
            sb.append(base);
            break;
        case CENTRE_ALIGN :
            int half = columns >> 1;
            for (int i=0; i<half; ++i) {
                sb.append(' ');
            }
            if (half != columns / 2.0D) {
                sb.append(' ');
            }
            sb.append(base);
           for (int i=0; i<half; ++i) {
               sb.append(' ');
           }
           break;
        default :
            throw new IllegalArgumentException("Invalid columnising style: " + style);
        }
        return sb.toString();
    }
}
