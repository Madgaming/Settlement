package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;

public class Usage extends SettlementCommand {

    {
        usage = new String[] {
                "§2 - /settlement usage <command>:",
                "§a  Get the usage information for the settlement command <command>.",
                "§a  For a list of all commands, use /settlement help [page number]"
        };
        aliases = new String[] {"usage"};
    }
    
    public Usage(LocalCommand parent) {
        super(parent);
    }
    
    
}
