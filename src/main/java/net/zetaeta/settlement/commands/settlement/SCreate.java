package net.zetaeta.settlement.commands.settlement;

import static net.zetaeta.libraries.ZPUtil.arrayAsString;
import static net.zetaeta.settlement.util.SettlementUtil.checkPermission;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementData;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SCreate extends SettlementCommand {
    
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
    public SCreate(LocalCommandExecutor parent) {
        super(parent);
        parent.registerSubCommand(this);
    }


    /**
     * {@inheritDoc}
     * */
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        SettlementPlugin.plugin.log.info("SCreate");
        if (!checkPermission(sender, permission, true)) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(getUsage());
        }
        
        if (sender instanceof Player) {
            createSettlement((Player) sender, args);
            return true;
        }
        return false;
            
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
        Settlement settlement = new Settlement(sPlayer, arrayAsString(args));
        sPlayer.addData(new SettlementData(settlement, SettlementRank.OWNER));
    }

}
