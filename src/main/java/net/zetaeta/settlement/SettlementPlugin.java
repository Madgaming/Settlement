package net.zetaeta.settlement;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.zetaeta.libraries.ManagedJavaPlugin;
import net.zetaeta.libraries.commands.CommandsManager;
import net.zetaeta.settlement.commands.SettlementCommandsManager;
import net.zetaeta.settlement.listeners.SettlementBlockListener;
import net.zetaeta.settlement.listeners.SettlementPlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class SettlementPlugin extends ManagedJavaPlugin {
    
    public static Logger log;
    public static SettlementPlugin plugin;
//    public static ExecutorService settlementThreadPool = Executors.newCachedThreadPool();
    private PluginManager pm;
    public FileConfiguration config;
    public CommandsManager commandsManager;
    protected SettlementCommandsManager sCommandExec;
    
    /**
     * Contains the unloading procedure for the plugin.
     * */
    
    @Override
    public void onDisable() {
        try {
            Settlement.saveSettlements();
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "Error saving Settlements!", e);
            e.printStackTrace();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (SettlementPlayer.getSettlementPlayer(player) != null) {
                SettlementPlayer.getSettlementPlayer(player).unregister();
            }
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
        Future<?> settlementLoader = SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                int settlementCount = Settlement.loadSettlements();
                log.info("Loaded" + settlementCount + "Settlements!");
                int playerCount = 0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!SettlementPlayer.playerMap.containsKey(player)) {
                        new SettlementPlayer(player).register();
                        ++playerCount;
                    }
                }
                log.info("Loaded " + playerCount + " players!");
            }
        });
        config = getConfig();
        pm = getServer().getPluginManager();
        pm.registerEvents(new SettlementPlayerListener(), this);
        pm.registerEvents(new SettlementBlockListener(), this);
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

}
