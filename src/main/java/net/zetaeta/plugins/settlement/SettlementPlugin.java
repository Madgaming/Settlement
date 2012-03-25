package net.zetaeta.plugins.settlement;

import java.util.logging.Logger;

import net.zetaeta.plugins.libraries.ZetaPlugin;
import net.zetaeta.plugins.libraries.commands.CommandExecutor;
import net.zetaeta.plugins.settlement.commands.SettlementCommands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

public class SettlementPlugin extends ZetaPlugin {
	
	public Logger log;
	private PluginManager pm;
	public FileConfiguration config;
	public static SettlementPlugin plugin;
	public CommandExecutor commandHandler;
	protected SettlementCommands sCommandExec;
	
	/**
	 * Contains the unloading procedure for the plugin.
	 * */
	
	@Override
	public void onDisable() {
		Databases.destroy();
	}

	/**
	 * Contains the loading procedure for the plugin.
	 * */
	
	@Override
	public void onEnable() {
		log = getLogger();
		config = getConfig();
		pm = getServer().getPluginManager();
		Databases.initialize();
		Databases.loadDatabases();
		commandHandler.registerCommandExecutor(sCommandExec);
		
		log.info(this + " is now enabled!");
	}

	@Override
	public Logger getPluginLogger() {
		return log;
	}
	
	

}
