package net.zetaeta.plugins.settlement.commands.settlement;

import static net.zetaeta.plugins.settlement.SettlementUtil.checkSettlementPermission;
import static net.zetaeta.plugins.libraries.ZPUtil.*;
import net.zetaeta.plugins.settlement.Settlement;
import net.zetaeta.plugins.settlement.SettlementPlayer;
import net.zetaeta.plugins.settlement.commands.SettlementCommand;
import net.zetaeta.plugins.settlement.commands.SettlementCommands;
import net.zetaeta.plugins.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SCreate implements SettlementCommand {

	private String[] subArgs;
	private SettlementPermission permission;
	private String[] usage;
//	private String[] shortUsage = {};
	private String[] aliases = {"create", "new"};
	private SettlementCommand parent;
	private SettlementCommand[] children = {};
	
	/**
	 * Represents the single existing instance of the class for convenient public usage.
	 * */
	public static SCreate scCreate;
	
	
	{
		permission = new SettlementPermission(SettlementCommands.OWNER_PERMISSION, "create");
		usage = new String[] {
				"§2========§6Settlement§2========",
				"§a-------§dCommand Help§a-------",
				"§a  Create a settlement with the given name, with you as owner.",
				"§a - /settlement create <settlement name>",
				"§2=============================="
		};
	}
	
	/**
	 * Initialises the command handler class, registering it with SettlementCommands.
	 * 
	 * @param parent The SettlementCommands instance, for convenient registration.
	 * */
	public SCreate(SettlementCommand parent) {
		this.parent = parent;
		scCreate = this;
		parent.registerSubCommand(this);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String[] getArgs() {
		return subArgs;
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

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean doCommand(CommandSender sender, String subCommand, String[] args) {
		if (!checkSettlementPermission(sender, permission, true)) {
			return true;
		}
		if (args.length < 1) {
//			sender.sendMessage(getUsageShort());
			sender.sendMessage(getUsage());
		}
/*		if (args.length > 1) {
			sender.sendMessage(getUsage());
		}*/
		
		if (sender instanceof Player) {
			createSettlement((Player) sender, args);
			return true;
		}
		if (checkSettlementPermission(sender, new SettlementPermission(permission, "server"), true)) {
			createServerSettlement(args);
			return true;
		}
		return false;
			
	}

/*	private String[] getUsageShort() {
		return shortUsage;
	}*/

	/**
	 * Creates a public server settlements
	 * 
	 * @param args Name of settlement in String[] form.
	 * */
	public static void createServerSettlement(String[] args) {
		@SuppressWarnings("unused")
		Settlement settlement = new Settlement(SettlementPlayer.SERVER, arrayAsString(args));
	}


	/**
	 * Creates a settlement
	 * 
	 * @param owner Playernto own the settlement.
	 * 
	 * @param args Name of settlement in String[] form.
	 * */
	@SuppressWarnings("unused")
	public static void createSettlement(Player owner, String[] args) {
		Settlement settlement = new Settlement(SettlementPlayer.getSettlementPlayer(owner), arrayAsString(args));
	}

	@Override
	public SettlementCommand[] getChildren() {
		return children ;
	}

	@Override
	public SettlementCommand getParent() {
		return parent;
	}

	@Override
	public void registerSubCommand(SettlementCommand subCmd) {
		// TODO Auto-generated method stub
		
	}


	
	
}
