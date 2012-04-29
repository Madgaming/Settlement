package net.zetaeta.settlement;

import java.util.logging.Logger;

import net.zetaeta.libraries.ManagedJavaPlugin;
import net.zetaeta.libraries.commands.CommandsManager;
import net.zetaeta.settlement.commands.SettlementCommandsManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

public class SettlementPlugin extends ManagedJavaPlugin {
	
	public static Logger log;
	private PluginManager pm;
	public FileConfiguration config;
	public static SettlementPlugin plugin;
	public CommandsManager commandsManager;
	protected SettlementCommandsManager sCommandExec;
	
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
		plugin = this;
		log.info("LOADING...");
		config = getConfig();
		pm = getServer().getPluginManager();
//		Databases.initialize();
//		Databases.loadDatabases();
		commandsManager = new CommandsManager(this);
		sCommandExec = new SettlementCommandsManager();
		try {
		    commandsManager.registerCommands(sCommandExec);
		}
		catch (Throwable t) {
		    t.printStackTrace();
		}
		log.info("Commands Registered");
		
		log.info(this + " is now enabled!");
	}

	
	

}
