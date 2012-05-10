package net.zetaeta.settlement.object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;

import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.FlatFileIO;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SettlementServer implements SettlementConstants {
    private Map<String, Settlement> settlementsByName = new ConcurrentSkipListMap<String, Settlement>();
    private Map<Integer, Settlement> settlementsByUID = new ConcurrentHashMap<Integer, Settlement>();
    private Map<World, SettlementWorld> worlds = new ConcurrentHashMap<World, SettlementWorld>();
    private Map<Chunk, Settlement> settlementChunkCache;
    private Map<Player, SettlementPlayer> players = new HashMap<Player, SettlementPlayer>();
    private SettlementPlugin plugin;
    
    public SettlementServer(SettlementPlugin settlementPlugin) {
        plugin = settlementPlugin;
    }
    
    public void init() {
      int settlementCount = loadSettlements();
      log.info("Loaded " + settlementCount + " Settlements!");
      int playerCount = 0;
      for (Player player : Bukkit.getOnlinePlayers()) {
          if (server.getSettlementPlayer(player) == null) {
              registerPlayer(new SettlementPlayer(player));
              ++playerCount;
          }
      }
      for (World world : Bukkit.getWorlds()) {
          worlds.put(world, new SettlementWorld(world));
      }
      log.info("Loaded " + playerCount + " players!");
    }
    
    public void shutdown() {
        try {
            saveSettlements();
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "Error saving Settlements!", e);
            e.printStackTrace();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (server.getSettlementPlayer(player) != null) {
                unregisterPlayer(getSettlementPlayer(player));
            }
        }
    }
    
    public void registerSettlement(Settlement settlement) {
        settlementsByName.put(settlement.getName().toLowerCase(), settlement);
        settlementsByUID.put(settlement.getUid(), settlement);
    }
    

    public void unregisterSettlement(Settlement settlement) {
        settlementsByName.remove(settlement.getName().toLowerCase());
        settlementsByUID.remove(settlement.getUid());
    }

    /**
     * Gets the Settlement with the given name
     * 
     * @param name Name of the Settlement to get.
     * @return Settlement with the given name, or null if no Settlement has that name.
     */
    public Settlement getSettlementExact(String name) {
        return settlementsByName.get(name.toLowerCase());
    }
    
    
    public Settlement getSettlement(String name) {
        String lowerName = name.toLowerCase();
        Settlement result = null;
        int length = Integer.MAX_VALUE;
        for (Settlement poss : settlementsByUID.values()) {
            String possLower = poss.getName().toLowerCase();
            if (possLower.startsWith(lowerName)) {
                int currlength = possLower.length() - name.length();
                if (currlength < length) {
                    length = currlength;
                    result = poss;
                }
                if (length == 0) {
                    break;
                }
            }
        }
        return result;
    }
    
    
    /**
     * Gets the Settlement with the given unique ID.
     * 
     * @param uid UID of the Settlement to get.
     * @return Settlement with the given UID, or null if none has it.
     */
    public Settlement getSettlement(int uid) {
        return settlementsByUID.get(uid);
    }
    
    public SettlementWorld getWorld(World world) {
        return worlds.get(world);
    }
    
    /**
     * Gets a {@link java.util.Collection} of all Settlements currently loaded.
     * 
     * @return Collection of all Settlements.
     */
    public Collection<Settlement> getSettlements() {
        return settlementsByUID.values();
    }

    /**
     * Gets an ordered {@link Collection} of all Settlements currently loaded in alphabetical order of their name.
     * 
     * @return Ordered collection of Settlements
     */
    public Collection<Settlement> getOrderedSettlements() {
        return settlementsByName.values();
    }

//    /**
//     * Gets a {@link Collection} of all the Settlements that have plots in a given world.
//     * <p />
//     * WARNING: This method is <b>very</b> slow if {@link ConfigurationConstants#useSettlementWorldCacheing Settlement world cacheing} is disabled
//     * and should not be used unless absolutely necessary, especially in the main thread.
//     * 
//     * @param world {@link World} to get Settlements in.
//     * @return Collection of Settlements with plots in world.
//     */
//    public Collection<Settlement> getSettlementsIn(World world) {
//        if (ConfigurationConstants.useSettlementWorldCacheing) {
//            return null;//TODO settlementsByWorld.get(world);
//        }
//        else {
//            Collection<Settlement> worldSets = new HashSet<Settlement>();
//            for (Settlement set : settlementsByUID.values()) {
//                for (Chunk chunk : set.getPlots()) {
//                    if (chunk.getWorld().equals(world)) {
//                        worldSets.add(set);
//                        break;
//                    }
//                }
//            }
//            return worldSets;
//        }
//    }


    public SettlementPlayer getSettlementPlayer(Player player) {
        if (player == null) {
            return null;
        }
        if (players.containsKey(player))
            return players.get(player);
        SettlementPlayer sp = new SettlementPlayer(player);
        registerPlayer(sp);
        return sp;
    }
    
    
    public SettlementPlayer getSettlementPlayer(String name) {
        return getSettlementPlayer(Bukkit.getPlayer(name));
    }
    
    public SettlementPlayer getSettlementPlayerExact(String name) {
        return getSettlementPlayer(Bukkit.getPlayerExact(name));
    }
    
    public Collection<SettlementPlayer> getOnlinePlayers() {
        return players.values();
    }
    
    public void registerPlayer(SettlementPlayer player) {
        player.loadFromFile();
        players.put(player.getPlayer(), player);
        for (SettlementData data : player.getData()) {
            data.getSettlement().addOnlinePlayer(player);
        }
    }
    
    public void unregisterPlayer(SettlementPlayer player) {
        player.saveToFile();
        players.remove(player.getPlayer());
        for (SettlementData data : player.getData()) {
            data.getSettlement().removeOnlinePlayer(player);
        }
    }
    
    /**
     * Loads all Settlements from ./Settlement/data/settlements.dat
     * @return number of settlements loaded.
     */
    public int loadSettlements() {
        log.info("Loading Settlements...");
        File settlementsFile = new File(plugin.getSettlementsFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
            settlementsFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
            try {
                settlementsFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create settlements.dat file!", e);
                e.printStackTrace();
            }
            return 0;
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(settlementsFile));
        } catch (FileNotFoundException e) {
            log.severe("Could not open settlements.dat file!");
            return 0;
        }
        
        int count = 0;
        try {
            if (dis.available() > 0) {
                log.info("Bytes left to read: " + dis.available());
                int version = dis.readInt();
                log.info("Read version: " + version);
                if (version == 0) {
                    while(dis.available() > 0) {
                        log.info("Bytes left to read: " + dis.available());
                        Settlement set = null;
                        try {
                            set = FlatFileIO.loadSettlementV0_0(dis);
                            registerSettlement(set);
                            ++count;
                            log.info("Loaded settlement " + set.getName());
                        }
                        catch (Throwable thrown) {
                            log.severe("Error occurred while loading settlement " + (set == null ? "" : set.getName()) + ": " + thrown.getClass().getName());
                            log.log(Level.SEVERE, "Error loading settlements!", thrown);
                            while (dis.readChar() != '\n') {
                                
                            }
                        }
                    }
                }
                else {
                    log.severe("Error reading from settlements.dat: Unsupported format version: " + version);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading Settlements!", e);
            e.printStackTrace();
        }
        finally {
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * Saves all Settlements to ./Settlement/data/settlements.dat
     */
    public void saveSettlements() {
        log.info("Saving Settlements...");
        File settlementsFile = new File(plugin.getSettlementsFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
            try {
                settlementsFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create settlements.dat file!", e);
                e.printStackTrace();
                return;
            }
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(settlementsFile));
        } catch (FileNotFoundException e) {
            log.severe("Could not open settlements.dat file!");
            return;
        }
        try {
            dos.writeInt(FlatFileIO.SETTLEMENT_FILE_VERSION);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        for (Iterator<Settlement> settlements = settlementsByUID.values().iterator(); settlements.hasNext();) {
            Settlement settlement = settlements.next();
            log.info("Saving settlement " + settlement.getName());
            try {
                FlatFileIO.saveSettlementV0_0(settlement, dos);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error while saving Settlement " + settlement.getName() + "!", e);
                e.printStackTrace();
            }
            if (settlements.hasNext()) {
                try {
                    dos.writeChar('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File oldFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
        if (oldFile.exists()) {
            oldFile.delete();
        }
    }

    /**
     * Gets a random new Unique ID for creating a new Settlement, guaranteed to be different from the UID of any other current Settlement.
     * 
     * @return new random unique ID.
     */
    public int getNewUID() {
        System.out.println("getNewUID");
        Random uidGen = new Random();
        int possible = 0;
        do {
            possible = uidGen.nextInt();
        }
        while (settlementsByUID.containsKey(possible) && possible != 0);
        return possible;
    }
    
    public void changeSettlementName(Settlement settlement, String newName) {
        settlementsByName.remove(settlement.getName());
        settlementsByName.put(newName, settlement);
    }
    
    
    public Settlement getOwner(Chunk chunk) {
        if (ConfigurationConstants.useChunkOwnershipCacheing) {
            if (settlementChunkCache == null) {
                settlementChunkCache = new HashMap<Chunk, Settlement>((int) (ConfigurationConstants.chunkOwnershipCacheSize / 0.75));
            }
            if (settlementChunkCache.containsKey(chunk)) {
                return settlementChunkCache.get(chunk);
            }
        }
            for (Settlement settlement : server.getSettlements()) {
                if (settlement.ownsChunk(chunk)) {
                    if (ConfigurationConstants.useChunkOwnershipCacheing) {
                        settlementChunkCache.put(chunk, settlement);
                    }
                    return settlement;
                }
            }
            return null;
//        }
    }
    
    public void clearFromChunkCache(Settlement settlement) {
        if (settlementChunkCache == null) {
            return;
        }
        for (Iterator<Settlement> setIt = settlementChunkCache.values().iterator(); setIt.hasNext();) {
            if (setIt.next().equals(settlement)) {
                setIt.remove();
            }
        }
    }
    
    public void clearFromChunkCache(Chunk chunk) {
        if (settlementChunkCache == null) {
            return;
        }
        settlementChunkCache.remove(chunk);
    }
}
