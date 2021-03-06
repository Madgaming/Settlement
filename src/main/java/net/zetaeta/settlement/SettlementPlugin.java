package net.zetaeta.settlement;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.zetaeta.libraries.ManagedJavaPlugin;
import net.zetaeta.libraries.commands.CommandsManager;
import net.zetaeta.settlement.commands.SettlementCommandsManager;
import net.zetaeta.settlement.listeners.SettlementBlockListener;
import net.zetaeta.settlement.listeners.SettlementPlayerListener;
import net.zetaeta.settlement.listeners.SettlementWorldListener;
import net.zetaeta.settlement.object.SettlementServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

public class SettlementPlugin extends ManagedJavaPlugin {
    public static Logger log;
    public static SettlementPlugin plugin;
    private PluginManager pm;
    private FileConfiguration config;
    private CommandsManager commandsManager;
    protected SettlementCommandsManager sCommandExec;
    private SettlementServer server;
    
    /**
     * Contains the unloading procedure for the plugin.
     */
    
    @Override
    public void onDisable() {
        if (ConfigurationConstants.useMultithreading && ConfigurationConstants.multithreadedShutdown) {
            SettlementThreadManager.submitAsyncTask(new Runnable() {
                public void run() {
                    server.shutdown();
                }
            });
        }
        else {
            server.shutdown();
        }
    }

    /**
     * Contains the loading procedure for the plugin.
     * */
    
    @Override
    public void onEnable() {
        log = getLogger();
        plugin = this;
        log.info("LOADING...");
        SettlementThreadManager.init();
        server = new SettlementServer(this);
        Future<?> settlementLoader = SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                server.init();
            }
        });
        log.info(getDescription().getLoad().toString());
        config = getConfig();
        pm = getServer().getPluginManager();
        pm.registerEvents(new SettlementPlayerListener(), this);
        pm.registerEvents(new SettlementBlockListener(), this);
        pm.registerEvents(new SettlementWorldListener(), this);
        commandsManager = new CommandsManager(this);
        sCommandExec = new SettlementCommandsManager();
        try {
            commandsManager.registerCommands(sCommandExec);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        log.info("Commands Registered");
        try {
            settlementLoader.get();
        } catch (InterruptedException | ExecutionException e) {
            log.log(Level.SEVERE, "SEVERE ERROR: Could not load settlements: " + e.getClass().getName(), e);
            log.severe("Settlement will now disable, restart the server to try loading again");
            getServer().getPluginManager().disablePlugin(plugin);
            e.printStackTrace();
        }
        log.info(this + " is now enabled!");
    }

    public File getSavedDataFolder() {
        File sdFolder = new File(getDataFolder(), "data");
        sdFolder.mkdirs();
        return sdFolder;
    }
    
    public File getPlayersFolder() {
        File pFolder = new File(getSavedDataFolder(), "players");
        pFolder.mkdirs();
        return pFolder;
    }
    
    public File getSettlementsFolder() {
        File sFolder = new File(getSavedDataFolder(), "settlements");
        sFolder.mkdirs();
        return sFolder;
    }
    
    public File getPlotsFolder() {
        File pFolder = new File(getSavedDataFolder(), "plots");
        pFolder.mkdirs();
        return pFolder;
    }
    
    public SettlementServer getSettlementServer() {
        return server;
    }
}
