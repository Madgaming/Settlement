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

import org.bukkit.Chunk;
import org.bukkit.World;

public class SettlementServer implements SettlementConstants {
    private Map<String, Settlement> settlementsByName = new ConcurrentSkipListMap<String, Settlement>();
    private Map<Integer, Settlement> settlementsByUID = new ConcurrentHashMap<Integer, Settlement>();
    private SettlementPlugin plugin;
    
    public SettlementServer(SettlementPlugin settlementPlugin) {
        plugin = settlementPlugin;
    }
    
    public void registerSettlement(Settlement settlement) {
        settlementsByName.put(settlement.getName().toLowerCase(), settlement);
        settlementsByUID.put(settlement.getUid(), settlement);
    }

    public void unregisterSettlement(Settlement settlement) {
        settlementsByName.remove(settlement.getName());
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

    /**
     * Gets a {@link Collection} of all the Settlements that have plots in a given world.
     * <p />
     * WARNING: This method is <b>very</b> slow if {@link ConfigurationConstants#useSettlementWorldCacheing Settlement world cacheing} is disabled
     * and should not be used unless absolutely necessary, especially in the main thread.
     * 
     * @param world {@link World} to get Settlements in.
     * @return Collection of Settlements with plots in world.
     */
    public Collection<Settlement> getSettlementsIn(World world) {
        if (ConfigurationConstants.useSettlementWorldCacheing) {
            return null;//TODO settlementsByWorld.get(world);
        }
        else {
            Collection<Settlement> worldSets = new HashSet<Settlement>();
            for (Settlement set : settlementsByUID.values()) {
                for (Chunk chunk : set.getPlots()) {
                    if (chunk.getWorld().equals(world)) {
                        worldSets.add(set);
                        break;
                    }
                }
            }
            return worldSets;
        }
    }

    /**
     * Loads all Settlements from ./Settlement/data/settlements.dat
     * @return 
     */
    public int loadSettlements() {
        log.info("Loading Settlements...");
        File settlementsFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
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
                int version = dis.readInt();
                if (version == 0) {
                    while(dis.available() > 0) {
                        FlatFileIO.loadSettlementV0_0(dis);
                        ++count;
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
        File settlementsFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
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
            dos.writeInt(FlatFileIO.FILE_FORMAT_VERSION);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        for (Settlement settlement : settlementsByUID.values()) {
//            if (!settlement.shouldSave)
//                continue;
            try {
                FlatFileIO.saveSettlementV0_0(settlement, dos);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error while saving Settlement " + settlement.getName() + "!", e);
                e.printStackTrace();
            }
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
        while (settlementsByUID.containsKey(possible));
        return possible;
    }
    
    public void changeSettlementName(Settlement settlement, String newName) {
        settlementsByName.remove(settlement.getName());
        settlementsByName.put(newName, settlement);
    }
    
    private Map<Chunk, Settlement> settlementCache;
    
    public Settlement getOwner(Chunk chunk) {
        if (ConfigurationConstants.useChunkOwnershipCacheing) {
            if (settlementCache == null) {
                settlementCache = new HashMap<Chunk, Settlement>((int) (ConfigurationConstants.chunkOwnershipCacheSize / 0.75));
            }
            if (settlementCache.containsKey(chunk)) {
                return settlementCache.get(chunk);
            }
        }
//        if (ConfigurationConstants.useSettlementWorldCacheing) {
//            for (Settlement settlement : Settlement.getSettlementsIn(chunk.getWorld())) {
//                if (settlement.ownsChunk(chunk)) {
//                    if (ConfigurationConstants.useChunkOwnershipCacheing) {
//                        settlementCache.put(chunk, settlement);
//                    }
//                    return settlement;
//                }
//            }
//            return null;
//        }
//        else {
            for (Settlement settlement : server.getSettlements()) {
                if (settlement.ownsChunk(chunk)) {
                    if (ConfigurationConstants.useChunkOwnershipCacheing) {
                        settlementCache.put(chunk, settlement);
                    }
                    return settlement;
                }
            }
            return null;
//        }
    }
    
    public void clearFromChunkCache(Settlement settlement) {
        if (settlementCache == null) {
            return;
        }
        for (Iterator<Settlement> setIt = settlementCache.values().iterator(); setIt.hasNext();) {
            if (setIt.next().equals(settlement)) {
                setIt.remove();
            }
        }
    }
    
    public void clearFromChunkCache(Chunk chunk) {
        if (settlementCache == null) {
            return;
        }
        settlementCache.remove(chunk);
    }
}
