package net.zetaeta.settlement.commands;

import java.util.Set;

import net.zetaeta.libraries.commands.local.AbstractLocalCommandExecutor;

import org.bukkit.command.CommandSender;

public abstract class SettlementCommand extends AbstractLocalCommandExecutor {
	
	/**
	 * Gets the list of all the command's possible subcommand's aliases. 
	 * Equivalent to running <code>getAliases()</code> on each of the results of <code>getChildren()</code>
	 * 
	 * @return String[] of subcommand aliases.
	 */
	public abstract String[] getArgs();
	
	/**
	 * Gets an array of the direct children commands of this command.
	 * 
	 * @return Array of subcommands.
	 */
	public abstract SettlementCommand[] getChildren();
	
	/**
	 * Gets the {@link net.zetaeta.settlement.commands.SettlementPermission SettlementPermission} associated with this command.
	 * 
	 * @return The commands's SettlementPermission
	 */
	public abstract SettlementPermission getPermission();
	
	/**
	 * Gets the usage list for the command, used for improperly formed command or {@literal /settlement help <command>}
	 * 
	 * @return Command's usage info.
	 */
	public abstract String[] getUsage();
	
	/**
	 * Gets different possible aliases for the subcommand.
	 * 
	 * @return Aliases of the command.
	 */
	public abstract Set<String> getAliases();
	
	/**
	 * Gets the parent command of the subcommand. 
	 * e.g. /settlement set owner would return /settlement set.
	 * 
	 * @return Parent command.
	 */
	public abstract SettlementCommand getParent();

	/**
	 * Used to register a subcommand to this command.
	 * Even if a command should not have any subcommands, it should still be properly implemented for future convenience or to make extensions easier.
	 * 
	 * @param subCmd Subcommand to be registered.
	 */
	public abstract void registerSubCommand(SettlementCommand subCmd);

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
	 */
	public abstract boolean doCommand(CommandSender sender, String subCommand, String[] args);
	
}
