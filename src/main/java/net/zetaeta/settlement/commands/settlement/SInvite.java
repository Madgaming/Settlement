package net.zetaeta.settlement.commands.settlement;

import java.util.HashSet;
import java.util.Set;

import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementUtil;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementCommandsManager;
import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SInvite extends SettlementCommand {
	
	private Set<String> subArgs = new HashSet<String>();
	private String[] usage;
	private Set<String> aliases = new HashSet<String>();
	private SettlementPermission permission;
	private Set<SettlementCommand> children = new HashSet<SettlementCommand>();
	private SettlementCommand parent;
	
	public static SInvite sInvite;
	
	{
	    aliases.add("invite");
	    aliases.add("add");
	}
	
	public SInvite(SettlementCommand parent) {
		permission = new SettlementPermission("invite", SettlementCommandsManager.OWNER_PERMISSION);
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
	public Set<String> getAliases() {
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
		if (!SettlementUtil.checkSettlementPermission(sender, permission, true)) {
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(usage);
			return true;
		}
		SettlementPlayer sPlayer = null;
		if (sender instanceof Player) {
			sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
		}
		if (sPlayer != null && sPlayer.getFocus() != null) {
			Settlement invitedTo = sPlayer.getFocus();
			if (args[0].equalsIgnoreCase("-e")) {
				invitedTo.addInvitation(args[1]);
			}
		}
	}
}
