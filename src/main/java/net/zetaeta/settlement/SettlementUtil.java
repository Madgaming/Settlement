package net.zetaeta.settlement;

import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.command.CommandSender;

public class SettlementUtil {
	
	/**
	 * Checks whether a player has a particular SettlementPermission
	 * 
	 * @param sender Sender of the command
	 * 
	 * @param permission SettlementPermission to be checked
	 * 
	 * @return Whether the sender has the specific permission
	 * */
	public static boolean checkSettlementPermission(CommandSender sender, SettlementPermission permission, boolean isCommand) {
		if (checkSettlementPermissionNoMessage(sender, permission)) {
			return true;
		}
		if (isCommand)
			sender.sendMessage("§cYou do not have access to that command!");
		else
			sender.sendMessage("§cYou are not allowed to do that!");
		return false;
	}

	public static boolean checkSettlementPermissionNoMessage( CommandSender sender, SettlementPermission permission) {
		if (sender.hasPermission(permission.getPermission()))
			return true;
		SettlementPermission permIterator = permission;
		while (!permission.isMasterPermission()) {
			if (sender.hasPermission(permIterator.getPermission() + ".*"))
				return true;
			permIterator = permIterator.getParent();
		}
		return false;
	}
}
