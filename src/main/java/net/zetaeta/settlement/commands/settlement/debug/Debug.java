package net.zetaeta.settlement.commands.settlement.debug;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;

import org.bukkit.command.CommandSender;

public class Debug extends SettlementCommand {
    public static final String DEBUG_PERMISSION = ADMIN_BASIC_PERMISSION + ".debug";
    
    public Debug(LocalCommand parent) {
        super(parent);
        permission = DEBUG_PERMISSION;
        usage = new String[] {
                "§2 - /settlement debug",
                "§a  Debug stuff"
        };
        aliases = new String[] {"debug"};
        registerSubCommand(new ReloadSettlementData(this));
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        return false;
    }
}
