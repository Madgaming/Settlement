package net.zetaeta.settlement;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;

public abstract class Databases {
	public static File settlements;
	public static File players;
	public static SettlementPlugin plugin = SettlementPlugin.plugin;
	
	/**
	 * Equivalent to constructor, handles loading everything
	 * */
	protected static void initialize() {
		File dataFolder = new File(plugin.getDataFolder(), "data");
		dataFolder.mkdirs();
		settlements = new File(plugin.getDataFolder(), "data" + File.separator + "settlements");
		settlements.mkdirs();
		players = new File(plugin.getDataFolder(), "data" + File.separator + "players");
		players.mkdirs();
	}
	
	/**
	 * Loads settlement and player info from their respective .dat files.
	 * */
	public static void loadDatabases() {
		Thread playersLoader = new Thread(new Runnable() {

			@Override
			public void run() {
				File[] playersFiles = players.listFiles();
				int playerCount = 0;
				for (File playerFile : playersFiles) {
					if (playerFile.getName().endsWith(".plr")) {
						try {
							loadPlayerFile(playerFile);
							playerCount++;
						} catch(IOException e) {
							plugin.log.warning("An error occurred loading file " + playerFile.getName() + ":");
							plugin.log.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				plugin.log.info("Player files finished loading; " + playerCount + " players loaded!");
			}
			
			
			private void loadPlayerFile(File playerFile) throws IOException {
				ObjectInputStream playerInput = null;
				try {
					playerInput = new ObjectInputStream(new FileInputStream(playerFile));
				} catch (EOFException e) {
					plugin.log.warning("Player file " + playerFile.getName() + " is empty!");
					plugin.log.log(Level.WARNING, e.getMessage(), e);
					return;
				}
				SettlementPlayer output;
				try {
					output = (SettlementPlayer) playerInput.readObject();
					SettlementPlayer.loadPlayer(output);
				} catch (EOFException e) {
					
				} catch (ClassNotFoundException e) {
					plugin.log.log(Level.WARNING, e.getMessage(), e);
				}
			}
			
			
		});
		
		
		
		Thread settlementsLoader = new Thread(new Runnable() {

			@Override
			public void run() {
				File[] settlementsFiles = settlements.listFiles();
				int settlementCount = 0;
				for (File settlementFile : settlementsFiles) {
					if (settlementFile.getName().endsWith(".set")) {
						try {
							loadSettlementFile(settlementFile);
							settlementCount++;
						} catch(IOException e) {
							plugin.log.warning("An error occurred loading file " + settlementFile.getName() + ":");
							plugin.log.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				plugin.log.info("Settlement files finished loading; " + settlementCount + " settlements loaded!");
			}
			
			
			private void loadSettlementFile(File settlementFile) throws IOException {
				ObjectInputStream settlementInput = null;
				try {
					settlementInput = new ObjectInputStream(new FileInputStream(settlementFile));
				} catch (EOFException e) {
					plugin.log.warning("Settlement file " + settlementFile.getName() + " is empty!");
					plugin.log.log(Level.WARNING, e.getMessage(), e);
					return;
				}
				Settlement output;
				try {
					output = (Settlement) settlementInput.readObject();
					Settlement.loadSettlement(output);
				} catch (EOFException e) {
					
				} catch (ClassNotFoundException e) {
					plugin.log.log(Level.WARNING, e.getMessage(), e);
				}
			}
			
			
		});
		
		playersLoader.start();
		settlementsLoader.start();
		
/*		try {
			settlementsInput = new ObjectInputStream(new FileInputStream(settlements));
		} catch (EOFException e) {
			
		} catch (IOException e) {
			plugin.log.severe("An error occured while loading settlements file!");
			e.printStackTrace();
		}
		try {
			playersInput = new ObjectInputStream(new FileInputStream(settlements));
		} catch (EOFException e) {
			
		} catch (IOException e) {
			plugin.log.severe("An error occured while loading players file!");
			e.printStackTrace();
		}
		Thread readPlayers= new Thread(new Runnable() {

			@Override
			public void run() {
				SettlementPlayer output;
				int playerCount = 0;
				try {
					while ((output = (SettlementPlayer) playersInput.readObject()) != null) {
						loadPlayer(output);
						playerCount++;
					}
					plugin.log.info("Finished loading players!");
				} catch (EOFException e) {
					plugin.log.info("Finished loading players! " + playerCount + " players were loaded!");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		Thread readSettlements= new Thread(new Runnable() {

			@Override
			public void run() {
				Settlement output;
				int settlementCount = 0;
				if (settlements.getTotalSpace() == 0)
					return;
				try {
					while ((output = (Settlement) settlementsInput.readObject()) != null) {
						loadSettlement(output);
						settlementCount++;
					}
					plugin.log.info("Finished loading settlements!");
				} catch (EOFException e) {
					plugin.log.info("Finished loading settlements! " + settlementCount + " settlements were loaded!");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		readPlayers.start();
		readSettlements.start();*/
	}

	protected static void destroy() {
		
	}
	
}
