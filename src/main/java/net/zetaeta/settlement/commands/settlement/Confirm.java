package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Confirm extends SettlementCommand {

    {
        usage = new String[] {
                "§2 - /settlement confirm",
                "§a  \u00bbConfirm any settlement command awaiting confirmation."
        };
        shortUsage = usage;
        aliases = new String[] {"confirm"};
    }
    
    public Confirm(LocalCommand parent) {
        super(parent);
    }
    
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        if (!(sender instanceof Player)) {
            SettlementMessenger.sendSettlementMessage(sender, "§cThis command can only be run by a player.");
            return true;
        }
        if (args.length != 0) {
            SettlementMessenger.sendUsage(sender, usage);
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        if (sPlayer.hasConfirmTimedOut()) {
            SettlementMessenger.sendSettlementMessage(sender, "§cYou do not have anything to confirm!");
            return true;
        }
        Thread thread = new Thread(sPlayer.getConfirmable());
        thread.start();
        return true;
    }
    
}
