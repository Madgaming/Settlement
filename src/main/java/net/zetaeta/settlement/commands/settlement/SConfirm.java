package net.zetaeta.settlement.commands.settlement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalPermission;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SConfirm extends SettlementCommand {

    
	public SConfirm(LocalCommandExecutor parent) {
	    super(parent);
        aliases = new String[] {"confirm"};
	    parent.registerSubCommand(this);
	}

	@Override
	public LocalPermission getPermission() {
		return null;
	}

	@Override
	public void registerSubCommand(LocalCommandExecutor subCmd) {
	
	}
	
/*	@Override
	public Set<String> getSubCommandAliases() {
	    return new HashSet<String>(0);
	}
	
	@Override
	public Collection<LocalCommandExecutor> getSubCommands() {
	    return new ArrayList<LocalCommandExecutor>();
	}*/
	
	@Override
	public boolean execute(CommandSender sender, String subCommand, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cThis command can only be run by a player.");
			return true;
		}
		if (args.length != 0) {
			sender.sendMessage(getUsage());
		}
		SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
		if (sPlayer.hasConfirmTimedOut()) {
		    sender.sendMessage("§cYou do not have anything to confirm!");
		    return true;
		}
		Thread thread = new Thread(sPlayer.getConfirmable());
		thread.start();
		return true;
	}
	
}
