package net.zetaeta.settlement.object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.zetaeta.settlement.FlatFileIO;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.ToBeSaved;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SettlementPlayer implements SettlementConstants {
    public static final SettlementPlayer NONE;
    @ToBeSaved
    private String name;
    @ToBeSaved 
    protected long lastOnline;
    @ToBeSaved
    private Set<SettlementData> settlementsInfo = new HashSet<SettlementData>();
    
    private Player player;
    private Settlement focus;
    private Runnable confirmable;
    private boolean confirmTimedOut = true;
    private boolean bypass;
    
    static {
        NONE = new SettlementPlayer() {
            @Override
            public boolean equals(Object other) {
                return this == other;
            }
            public int hashCode() {
                return "".hashCode() + 0x123;
            }
            
        };
    }
    
    private SettlementPlayer() {
        name = "";
    }
    
    public SettlementPlayer(Player plr) {
        player = plr;
        name = plr.getName();
    }
    
//    public void register() {
//        loadFromFile();
//        server.registerPlayer(this);
//        for (SettlementData data : settlementsInfo) {
//            data.getSettlement().addOnlinePlayer(this);
//        }
//    }
//    
//    public void unregister() {
//        saveToFile();
//        server.unregisterPlayer(this);
//        for (SettlementData data : settlementsInfo) {
//            data.getSettlement().removeOnlinePlayer(this);
//        }
//    }
    
    protected void loadFromFile() {
        File playerFile = new File(SettlementPlugin.plugin.getPlayersFolder(), name + ".dat");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create data file for player " + name, e);
                e.printStackTrace();
            }
            return;
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(playerFile));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not find data file for player " + name, e);
            e.printStackTrace();
            return;
        }
        try {
            int fileVersion = dis.readInt();
            if (fileVersion == 0) {
                FlatFileIO.loadPlayerV0_0(this, dis);
            }
            else {
                log.severe("Error reading from player file " + name + "Unsupported format version: " + fileVersion);
                if (player != null) {
                    player.sendMessage("§4Error occurred loading Settlement info! Please report this to your administrator!");
                }
                return;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading from data file of player " + name, e);
            e.printStackTrace();
        }
        finally {
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void saveToFile() {
        File playerFile = new File(SettlementPlugin.plugin.getPlayersFolder(), name + ".dat");
        try {
            playerFile.createNewFile();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not create data file for player " + name, e);
            e.printStackTrace();
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(playerFile));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not find data file for player " + name, e);
            e.printStackTrace();
            try {
                dos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }
        try {
            FlatFileIO.savePlayerV0_0(this, dos);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error occurred during saving of player " + name, e);
            e.printStackTrace();
        }
        finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SettlementPlayer) {
            return player.equals(((SettlementPlayer) o).player);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 3 * player.hashCode();
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public String getName() {
        return name;
    }
    
    public long getLastOnline() {
        return lastOnline;
    }
    
    public void initialiseLastOnline(long time) {
        lastOnline = time;
    }
    
    public boolean isSettlementMember() {
        return getSettlements().length > 0;
    }
    
    
    public Settlement[] getSettlements() {
        List<Settlement> settlements = new ArrayList<Settlement>();
        for (SettlementData data : settlementsInfo) {
            settlements.add(data.getSettlement());
        }
        return settlements.toArray(new Settlement[0]);
    }
    
    public Settlement getSettlement(String name) {
        for (SettlementData data : settlementsInfo) {
            if (data.getSettlementName().equalsIgnoreCase(name)) {
                return data.getSettlement();
            }
        }
        return null;
    }
    
    public Rank getRank(String settlementName) {
        for (SettlementData data : settlementsInfo) {
            if (data.getSettlementName().equalsIgnoreCase(settlementName)) {
                return data.getRank();
            }
        }
        return null;
    }
    

    /**
     * Associates the specified SettlementData with this player
     * 
     * @param data SettlementData to be associated.
     * */
    public void addData(SettlementData data) {
         settlementsInfo.add(data);
    }
    
    /**
     * Gets the SettlementData containing the information for the specified Settlement.
     * 
     * @param settlement Settlement to get data for.
     * 
     * @return SettlementData associated with the settlement.
     */
    protected SettlementData getData(Settlement settlement) {
        for (SettlementData data : settlementsInfo) {
            if (data.getSettlement().equals(settlement)) {
                return data;
            }
        }
        return null;
    }
    
    /**
     * Gets the SettlementData containing the information for the specified Settlement.
     * 
     * @param settlementName Name of Settlement to get data for.
     * 
     * @return SettlementData associated with the settlement.
     */
    public SettlementData getData(String settlementName) {
        for (SettlementData data : settlementsInfo) {
            if (data.getSettlementName().equalsIgnoreCase(settlementName)) {
                return data;
            }
        }
        return null;
    }
    
    public Collection<SettlementData> getData() {
        return settlementsInfo;
    }
    
    public boolean removeData(SettlementData data) {
        return settlementsInfo.remove(data);
    }
    
    public boolean removeSettlement(Settlement settlement) {
        for (Iterator<SettlementData> sdIt = settlementsInfo.iterator(); sdIt.hasNext();) {
            if (sdIt.next().getSettlement().equals(settlement)) {
                sdIt.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a settlement to the list of settlements the player is member of, creating the relevant SettlementData and returning it.
     * 
     * @param settlement Settlement to add.
     * 
     * @return New SettlementData created with the specified Settlement.
     * */
    public SettlementData addSettlement(Settlement settlement) {
        SettlementData data = new SettlementData(settlement);
        addData(data);
        return data;
    }
    
    /**
     * Convenience method, sets the player's rank in the specified Settlement.
     * Equivalent to <code>getData(settlement).setRank(rank);</code>
     * 
     * @param settlement Settlement to assign rank in.
     * 
     * @param rank Rank to assign to player.
     * */
    public void setRank(Settlement settlement, Rank rank) {
        if (getData(settlement) != null) {
            getData(settlement).setRank(rank);
        }
        else {
            addData(new SettlementData(settlement, rank));
        }
    }
    
    public Rank getRank(Settlement settlement) {
        return getData(settlement) != null ? getData(settlement).getRank() : Rank.OUTSIDER;
    }
    
    public void setTitle (Settlement settlement, String title) {
        getData(settlement).setTitle(title);
    }


    public Settlement getFocus() {
        if (focus == null && settlementsInfo.size() == 1) {
            SettlementData singleSet = settlementsInfo.iterator().next();
            focus = singleSet.getSettlement();
        }
        return focus;
    }
    
    public void setFocus(Settlement focus) {
        this.focus = focus;
    }

    public void setConfirmable(Runnable runnable) {
        confirmTimedOut = false;
        confirmable = runnable;
    }
    
    public Runnable getConfirmable() {
        return confirmable;
    }


    public void setConfirmable(Runnable runnable, int timeout) {
        confirmable = runnable;
        confirmTimedOut = false;
        Bukkit.getScheduler().scheduleSyncDelayedTask(SettlementPlugin.plugin, new Runnable() {
            public void run() {
                confirmTimedOut = true;
            }
        }, timeout);
    }

    public boolean hasConfirmTimedOut() {
        return confirmTimedOut;
    }
    
    public boolean hasBypass() {
        return bypass;
    }
    
    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }
}
