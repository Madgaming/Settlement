package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.arrayAsString;
import static net.zetaeta.settlement.util.SettlementUtil.checkPermission;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementData;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends SettlementCommand {
    
    {
        permission = new SettlementPermission("create", SettlementPermission.USE_OWNER_PERMISSION);
        usage = new String[] {
                "§2 - /settlement create <settlement name>",
                "§a  Create a settlement with the given name, with you as owner.",
        };
        aliases = new String[] {"create", "new"};
    }
    
    /**
     * Initialises the command handler class, registering it with SettlementCommandsManager.
     * 
     * @param parent The SettlementCommandsManager instance, for convenient registration.
     * */
    public Create(LocalCommand parent) {
        super(parent);
    }


    /**
     * {@inheritDoc}
     * */
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        SettlementPlugin.log.info("Create");
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            SettlementPlugin.log.info("NoValid");
            return true;
        }
        SettlementPlugin.log.info("Valid");
        if (args.length < 1) {
            SettlementMessenger.sendUsage(sender, usage);
            SettlementPlugin.log.info("BadArgs");
            return true;
        }
        SettlementPlugin.log.info("GoodArgs");
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        SettlementPlugin.log.info("GotPlayer");
        String setName = SettlementUtil.arrayAsString(args);
        if (Settlement.getSettlement(setName) != null) {
            SettlementMessenger.sendSettlementMessage(sender, "§cA settlement with the name §6" + setName + " §calready exists!");
            return true;
        }
        Settlement settlement = new Settlement(sPlayer, SettlementUtil.arrayAsString(args), Settlement.getNewUID());
        SettlementPlugin.log.info("Created settlement " + settlement.getName());
//        settlement.addMember(sPlayer);
        sPlayer.addData(new SettlementData(settlement, SettlementRank.OWNER));
        SettlementPlugin.log.info("Data added ");
        settlement.broadcastSettlementMessage("§a  Settlement Created!");
        SettlementPlugin.log.info("Sent message");
        return true;
    }

    /**
     * Creates a settlement
     * 
     * @param owner Playernto own the settlement.
     * 
     * @param args Name of settlement in String[] form.
     * */
    public static void createSettlement(Player owner, String[] args) {
        SettlementPlugin.log.info("createSettlement");
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer(owner);
        Settlement settlement = new Settlement(sPlayer, arrayAsString(args), Settlement.getNewUID());
        sPlayer.addData(new SettlementData(settlement, SettlementRank.OWNER));
    }

}
