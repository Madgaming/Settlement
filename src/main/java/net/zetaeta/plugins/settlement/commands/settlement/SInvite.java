package net.zetaeta.plugins.settlement.commands.settlement;

import java.util.HashSet;
import java.util.Set;

import net.zetaeta.plugins.settlement.SettlementUtil;
import net.zetaeta.plugins.settlement.commands.SettlementCommand;
import net.zetaeta.plugins.settlement.commands.SettlementCommands;
import net.zetaeta.plugins.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;

public class SInvite implements SettlementCommand {
	
	private Set<String> subArgs = new HashSet<String>();
	private String[] usage;
	private String[] aliases = {"invite", "add"};
	private SettlementPermission permission;
	private Set<SettlementCommand> children = new HashSet<SettlementCommand>();
	private SettlementCommand parent;
	
	public static SInvite sInvite;
	
	public SInvite(SettlementCommand parent) {
		permission = new SettlementPermission(SettlementCommands.OWNER_PERMISSION, "invite");
		this.parent = parent;
		sInvite = this;
		parent.registerSubCommand(this);
	}
	
	@Override
	public String[] getArgs() {
		return subArgs.toArray(new String[0]);
	}

	@Override
	public SettlementCommand[] getChildren() {
		return children.toArray(new SettlementCommand[0]);
	}

	@Override
	public SettlementPermission getPermission() {
		return permission;
	}

	@Override
	public String[] getUsage() {
		return usage;
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public SettlementCommand getParent() {
		return parent;
	}

	@Override
	public void registerSubCommand(SettlementCommand subCmd) {
		children.add(subCmd);
		for (String s : subCmd.getAliases()) {
			subArgs.add(s);
		}
	}

	@Override
	public boolean doCommand(CommandSender sender, String subCommand, String[] args) {
		if (!SettlementUtil.checkSettlementPermission(sender, getPermission(), true)) {
			return true;
		}
		if (args.length < 1 || args.length > 3) {
			sender.sendMessage(getUsage());
			return true;
		}
		if (args[0].equalsIgnoreCase("-e")) {
			
		}
	}
}
