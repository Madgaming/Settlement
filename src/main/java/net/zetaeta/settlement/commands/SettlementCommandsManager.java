package net.zetaeta.settlement.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.zetaeta.libraries.commands.CommandHandler;
import net.zetaeta.libraries.commands.DynamicCommandExecutor;
import net.zetaeta.libraries.commands.Executor;
import net.zetaeta.libraries.commands.local.AbstractLocalCommandExecutor;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.commands.settlement.SCreate;
import net.zetaeta.settlement.commands.settlement.SDelete;
import net.zetaeta.settlement.commands.settlement.SInvite;

import static net.zetaeta.libraries.ZPUtil.*;
import static net.zetaeta.settlement.SettlementUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SettlementCommandsManager extends DynamicCommandExecutor implements LocalCommandExecutor {
	
	private final String[] aliases = null;
	private Map<String, SettlementCommand> argList = new HashMap<String, SettlementCommand>();
	private String[] usage;
	public static SettlementCommandsManager settlementCommandsManager;
	public static final SettlementPermission BASIC_PERMISSION;
	public static final SettlementPermission OWNER_PERMISSION;
	
	static {
		BASIC_PERMISSION = new SettlementPermission("use", SettlementPermission.MASTER_PERMISSION);
		OWNER_PERMISSION = new SettlementPermission("owner", SettlementPermission.MASTER_PERMISSION);
	}
	
	@SuppressWarnings("unused")
	protected SettlementCommandsManager() {
		settlementCommandsManager = this;
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
		return argList.values().toArray(new SettlementCommandsManager[0]);
	}

    @Override
    public Set<LocalCommandExecutor> getSubCommands() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerSubCommand(
            AbstractLocalCommandExecutor subCommandExecutor) {
        // TODO Auto-generated method stub
        
    }
	
	
	
}
