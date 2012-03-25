package net.zetaeta.plugins.settlement.commands.settlement;

import java.util.HashSet;
import java.util.Set;

import net.zetaeta.plugins.libraries.ZPUtil;
import net.zetaeta.plugins.settlement.Settlement;
import net.zetaeta.plugins.settlement.SettlementData;
import net.zetaeta.plugins.settlement.SettlementPlayer;
import net.zetaeta.plugins.settlement.SettlementRank;
import net.zetaeta.plugins.settlement.SettlementUtil;
import net.zetaeta.plugins.settlement.commands.SettlementCommand;
import net.zetaeta.plugins.settlement.commands.SettlementCommands;
import net.zetaeta.plugins.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SDelete implements SettlementCommand {
	
	private Set<String> subArgs = new HashSet<String>();
	private String[] aliases = {"delete", "disband"};
	private String[] usage;
	private SettlementCommand parent;
	private SettlementPermission permission;
	private Set<SettlementCommand> children = new HashSet<SettlementCommand>();
	
	public static SDelete scDelete;
	
	{
		permission = new SettlementPermission(SettlementCommands.OWNER_PERMISSION, "delete");
		usage = new String[] {
				"§2========§6Settlement§2========",
				"§a-------§dCommand Help§a-------",
				"§a - /settlement delete: Delete the settlement you are owner of.",
				"§2=============================="
		};
	}
	
	public SDelete(SettlementCommand parent) {
		this.parent = parent;
		scDelete = this;
		parent.registerSubCommand(this);
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String[] getArgs() {
		return subArgs.toArray(new String[0]);
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean doCommand(CommandSender sender, String subCommand, String[] args) {
		if (!SettlementUtil.checkSettlementPermissionNoMessage(sender, permission)) {
			if (!SettlementUtil.checkSettlementPermission(sender, permission, true)) {
				return true;
			}
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cThis command can only be run by a player.");
			return true;
		}
		
		SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
		
		if (args.length == 0) {
			if (sPlayer.getFocus() == null) {
				sender.sendMessage(getUsage());
			}
			SettlementData data = sPlayer.getData(sPlayer.getFocus());
			if (!data.getRank().isEqualOrSuperiorTo(SettlementRank.OWNER)) {
				sender.sendMessage("§2You do not have sufficient rights to do this!");
				return true;
			}
			getConfirmation(sPlayer, sPlayer.getFocus());
		}
		else {
			String settlementName = ZPUtil.arrayAsString(args);
			if (sender.hasPermission("settlement.admin.override")) {
				Settlement actedUpon = null;
				for (Settlement set : Settlement.allSettlements) {
					if (set.getName().equalsIgnoreCase(settlementName)) {
						actedUpon = set;
						break;
					}
				}
				if (actedUpon == null) {
					sender.sendMessage("§cThere is no settlement of that name!");
					return true;
				}
				getConfirmation(sPlayer, actedUpon);
			}
			else {
				Settlement actedUpon = null;
				Settlement[] settlements = sPlayer.getSettlements();
				for (Settlement set : settlements) {
					if (set.getName().equalsIgnoreCase(settlementName)) {
						actedUpon = set;
						break;
					}
				}
				if (actedUpon == null) {
					sender.sendMessage("§cYou are not in a settlement of that name!");
					return true;
				}
				SettlementData data = sPlayer.getData(actedUpon);
				if (!data.getRank().isEqualOrSuperiorTo(SettlementRank.OWNER)) {
					sender.sendMessage("§2You do not have sufficient rights to do this!");
					return true;
				}
				
				getConfirmation(sPlayer, actedUpon);
				return true;
			}
		}
		
		return false;
	}

	private static void getConfirmation(SettlementPlayer sPlayer, final Settlement settlement) {
		sPlayer.setConfirmable(new Runnable() {
			@Override
			public void run() {
				settlement.delete();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public SettlementPermission getPermission() {
		return permission;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String[] getUsage() {
		return usage;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public SettlementCommand[] getChildren() {
		return children.toArray(new SettlementCommand[0]);
	}

	@Override
	public SettlementCommand getParent() {
		return parent;
	}

	@Override
	public void registerSubCommand(SettlementCommand subCmd) {
		children.add(subCmd);
		String[] subAliases = subCmd.getAliases();
		for (String s : subAliases) {
			subArgs.add(s);
		}
	}
	
}
