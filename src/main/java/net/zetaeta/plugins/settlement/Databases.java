package net.zetaeta.plugins.settlement;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class Databases {
	public static File settlements;
	public static File players;
	public static SettlementPlugin plugin;
	public static Map<Player, SettlementPlayer> allPlayers;
	private static Set<SettlementPlayer> newPlayers;
	private static ObjectInputStream settlementsInput = null;
	private static ObjectInputStream playersInput = null;
	
	/**
	 * Equivalent to constructor, handles loading everything
	 * 
	 * @param thePlugin Main plugin to be referenced
	 * */
	
	protected static void initialize(SettlementPlugin thePlugin) {
		plugin = thePlugin;
		settlements = new File(plugin.getDataFolder(), "data" + File.pathSeparator + "settlements.dat");
		try {
			settlements.createNewFile();
		} catch (IOException e) {
			plugin.log.severe("ERROR: Could not make settlements file: " + e.toString() + " " + e.getMessage());
			e.printStackTrace();
		}
		players = new File(plugin.getDataFolder(), "data" + File.pathSeparator + "players.dat");
		try {
			players.createNewFile();
		} catch (IOException e) {
			plugin.log.severe("ERROR: Could not make players file: " + e.toString() + " " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads settlement and player info from their respective .dat files.
	 * */
	public static void loadDatabases() {

		try {
			settlementsInput = new ObjectInputStream(new FileInputStream(settlements));
		} catch (IOException e) {
			plugin.log.severe("An error occured while loading settlements file!");
			e.printStackTrace();
		}
		try {
			playersInput = new ObjectInputStream(new FileInputStream(settlements));
		} catch (IOException e) {
			plugin.log.severe("An error occured while loading players file!");
			e.printStackTrace();
		}
		Thread readPlayers= new Thread(new Runnable() {

			@Override
			public void run() {
				SettlementPlayer output;
				try {
					while ((output = (SettlementPlayer) playersInput.readObject()) != null) {
						loadPlayer(output);
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	protected static void loadPlayer(SettlementPlayer output) {
		allPlayers.put(plugin.getServer().getOfflinePlayer(output.getName()).getPlayer(), output);
		
	}

	protected static void destroy() {
		
	}

	public static void addNewPlayer(Player plr, SettlementPlayer sp) {
		allPlayers.put(plr, sp);
		newPlayers.add(sp);
	}
	
	
}
