package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.arrayAsString;
import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementData;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends SettlementCommand {
    
    {
        permission = OWNER_PERMISSION + ".create";
        usage = new String[] {
                "§2 - /settlement create <settlement name>",
                "§a  \u00bbCreate a settlement with the given name, with you as owner.",
        };
        shortUsage = new String[] {
                "§2 - /settlement create",
                "§a  \u00bbCreate a settlement"
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
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        if (args.length < 1) {
            SettlementMessenger.sendUsage(sender, usage);
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        String setName = SettlementUtil.arrayAsString(args);
        if (Settlement.getSettlement(setName) != null) {
            SettlementMessenger.sendSettlementMessage(sender, "§cA settlement with the name §6" + setName + " §calready exists!");
            return true;
        }
        Settlement settlement = new Settlement(sPlayer, SettlementUtil.arrayAsString(args), Settlement.getNewUID());
//        settlement.addMember(sPlayer);
        sPlayer.addData(new SettlementData(settlement, SettlementRank.OWNER));
        settlement.broadcastSettlementMessage("§a  Settlement Created!");
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
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer(owner);
        Settlement settlement = new Settlement(sPlayer, arrayAsString(args), Settlement.getNewUID());
        sPlayer.addData(new SettlementData(settlement, SettlementRank.OWNER));
    }

}
