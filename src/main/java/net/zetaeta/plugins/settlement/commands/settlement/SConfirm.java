package net.zetaeta.plugins.settlement.commands.settlement;

import net.zetaeta.plugins.settlement.SettlementPlayer;
import net.zetaeta.plugins.settlement.commands.SettlementCommand;
import net.zetaeta.plugins.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SConfirm implements SettlementCommand {

	private String[] usage;
	private SettlementCommand parent;
	
	public SConfirm(SettlementCommand parent) {
		this.parent = parent;
	}
	
	@Override
	public String[] getArgs() {
		return new String[] {};
	}

	@Override
	public SettlementCommand[] getChildren() {
		return null;
	}

	@Override
	public SettlementPermission getPermission() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return usage;
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public SettlementCommand getParent() {
		return parent;
	}

	@Override
	public void registerSubCommand(SettlementCommand subCmd) {
	}

	@Override
	public boolean doCommand(CommandSender sender, String subCommand, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cThis command can only be run by a player.");
			return true;
		}
		if (args.length != 0) {
			sender.sendMessage(getUsage());
		}
		SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
		Thread thread = new Thread(sPlayer.getConfirmable());
		thread.start();
		return true;
	}
	
}
