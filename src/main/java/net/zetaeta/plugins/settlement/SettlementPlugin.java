package net.zetaeta.plugins.settlement;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import net.zetaeta.plugins.libraries.ZetaPlugin;

public class SettlementPlugin extends ZetaPlugin {
	
	public Logger log;
	private PluginManager pm;
	public FileConfiguration config;
	public static SettlementPlugin inst;
	
	public void onDisable() {
		
	}

	public void onEnable() {
		log = getLogger();
		config = getConfig();
		pm = getServer().getPluginManager();
		Databases.initialize(this);
		Databases.loadDatabases();
		log.info(this + " is now enabled!");
	}

	@Override
	public Logger getPluginLogger() {
		return log;
	}
	
	

}
