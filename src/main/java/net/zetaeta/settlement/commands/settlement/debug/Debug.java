package net.zetaeta.settlement.commands.settlement.debug;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;

public class Debug extends SettlementCommand {
    public static final SettlementPermission DEBUG_PERMISSION = new SettlementPermission("debug", SettlementPermission.ADMIN_BASIC_PERMISSION);
    
    public Debug(LocalCommand parent) {
        super(parent);
        permission = DEBUG_PERMISSION;
        usage = new String[] {
                "debug"
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
