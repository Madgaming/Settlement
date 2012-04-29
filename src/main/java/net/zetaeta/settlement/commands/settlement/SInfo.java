package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementCommandsManager;
import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SInfo extends SettlementCommand {

    {
        permission = new SettlementPermission("info", SettlementPermission.USE_BASIC_PERMISSION);
        usage = new String[] {
                "§2 - /settlement info:",
                "§a  Get information about the settlement you have focus on",
        };
        aliases = new String[] {"info"};
    }
    
    public SInfo(LocalCommandExecutor parent) {
        super(parent);
        parent.registerSubCommand(this);
    }
    
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be run by a player!");
            }
            SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
            if (sPlayer.getFocus() != null) {
                sender.sendMessage(sPlayer.getFocus().getName());
                return true;
            }
            sender.sendMessage("focus == null");
        }
        return false;
    }
    
}
