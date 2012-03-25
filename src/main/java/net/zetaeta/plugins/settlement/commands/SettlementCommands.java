package net.zetaeta.plugins.settlement.commands;

import java.util.HashMap;
import java.util.Map;

import net.zetaeta.plugins.libraries.commands.CommandHandler;
import net.zetaeta.plugins.libraries.commands.Executor;
import net.zetaeta.plugins.settlement.commands.settlement.SCreate;
import net.zetaeta.plugins.settlement.commands.settlement.SDelete;
import net.zetaeta.plugins.settlement.commands.settlement.SInvite;

import static net.zetaeta.plugins.libraries.ZPUtil.*;
import static net.zetaeta.plugins.settlement.SettlementUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SettlementCommands implements Executor, SettlementCommand {
	
	private final String[] aliases = null;
	private Map<String, SettlementCommand> argList = new HashMap<String, SettlementCommand>();
	private String[] usage;
	public static SettlementCommands settlementCommands;
	public static final SettlementPermission BASIC_PERMISSION;
	public static final SettlementPermission OWNER_PERMISSION;
	
	static {
		BASIC_PERMISSION = new SettlementPermission(SettlementPermission.MASTER_PERMISSION, "basics");
		OWNER_PERMISSION = new SettlementPermission(SettlementPermission.MASTER_PERMISSION, "owner");
	}
	
	@SuppressWarnings("unused")
	protected SettlementCommands() {
		settlementCommands = this;
		SettlementCommand create = new SCreate(this);
		SettlementCommand delete = new SDelete(this);
		SettlementCommand invite = new SInvite(this);
	}
	
	/**
	 * Runs the standard settlement command (/settlement) and all possible arguments that follow it.
	 * 
	 * @param sender Sender of the command
	 * 
	 * @param command Command to be run.
	 * 
	 * @param cmdlbl Alias of command used.
	 * 
	 * @param args Arguments passed to the command in array form.
	 * 
	 * @return Whether command completes (unused).
	 * */
	
	@CommandHandler("settlement")
	public boolean settlementCommand(CommandSender sender, Command command, String cmdlbl, String[] args) {
		if (argList.containsKey(args[0])) {
			SettlementCommand sc = argList.get(args[0]);
			if (sc.doCommand(sender, args[0], removeFirstIndex(args))) {
				return true;
			}
			
			sender.sendMessage(sc.getUsage());
			return true;
		}
		sender.sendMessage(getUsage());
		return true;
	}
	
	/**
	 * @return Global /settlement usage help (page 1).
	 * */
	public String[] getUsage() {
		
		return usage;
	}

	
	/**
	 * Registers the subcommand for a specific executor.
	 * 
	 * @param subCmd Subcommand to be registered.
	 * 
	 * @param executor Executor to register subcommand for.
	 * */
	public void registerSubCommand(SettlementCommand executor) {
		
		for (String s : executor.getAliases()) {
			argList.put(s, executor);
		}
	}

	@Override
	public String[] getArgs() {
		return argList.keySet().toArray(new String[0]);
	}

	@Override
	public boolean doCommand(CommandSender sender, String subCommand, String[] args) {
		sender.sendMessage("You really shouldn't be seeing this.");
		return false;
	}

	@Override
	public SettlementPermission getPermission() {
		return SettlementPermission.MASTER_PERMISSION;
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public SettlementCommand getParent() {
		return this;
	}

	@Override
	public SettlementCommand[] getChildren() {
		return argList.values().toArray(new SettlementCommands[0]);
	}
	
	
	
}
