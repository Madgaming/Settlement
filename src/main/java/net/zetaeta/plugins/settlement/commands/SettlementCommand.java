package net.zetaeta.plugins.settlement.commands;

import net.zetaeta.plugins.libraries.commands.Executor;

import org.bukkit.command.CommandSender;

public interface SettlementCommand extends Executor {
	
	/**
	 * Gets the list of all the command's possible subcommand's aliases. 
	 * Equivalent to running <code>getAliases()</code> on each of the results of <code>getChildren()</code>
	 * 
	 * @return String[] of subcommand aliases.
	 * */
	public String[] getArgs();
	
	/**
	 * Gets an array of the direct children commands of this command.
	 * 
	 * @return Array of subcommands.
	 * */
	public SettlementCommand[] getChildren();
	
	/**
	 * Gets the {@link net.zetaeta.plugins.settlement.commands.SettlementPermission SettlementPermission} associated with this command.
	 * 
	 * @return The commands's SettlementPermission
	 * */
	public SettlementPermission getPermission();
	
	/**
	 * Gets the usage list for the command, used for improperly formed command or {@literal /settlement help <command>}
	 * 
	 * @return Command's usage info.
	 * */
	public String[] getUsage();
	
	/**
	 * Gets different possible aliases for the subcommand.
	 * 
	 * @return Aliases of the command.
	 * */
	public String[] getAliases();
	
	/**
	 * Gets the parent command of the subcommand. 
	 * e.g. /settlement set owner would return /settlement set.
	 * 
	 * @return Parent command.
	 * */
	public SettlementCommand getParent();

	/**
	 * Used to register a subcommand to this command.
	 * Even if a command should not have any subcommands, it should still be properly implemented for future convenience or to make extensions easier.
	 * 
	 * @param subCmd Subcommand to be registered.
	 * */
	public void registerSubCommand(SettlementCommand subCmd);

	/**
	 * Runs the subcommand
	 * 
	 * @param sender Sender of command
	 * 
	 * @param subCommand Specific command alias used to invoke this command.
	 * 
	 * @param args Arguments for the command.
	 * 
	 * @return Whether the subcommand completed
	 * */
	public boolean doCommand(CommandSender sender, String subCommand, String[] args);
	
}
