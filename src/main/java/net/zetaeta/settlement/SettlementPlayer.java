package net.zetaeta.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SettlementPlayer implements Externalizable {

    public static final String SERVER_NAME = "$SERVER$";
    public static final SettlementPlayer SERVER;
    public static Map<Player, SettlementPlayer> playerMap = new HashMap<Player, SettlementPlayer>();
    private static Set<SettlementPlayer> newPlayers = new HashSet<SettlementPlayer>();
    
//    @ToBeSaved
//    private List<SettlementPermission> permissions;
    @ToBeSaved
    private String name;
    @ToBeSaved
    private long lastOnline;
    @ToBeSaved
    private Set<SettlementData> settlementsInfo = new HashSet<SettlementData>();
    
    private Player player;
    private Settlement focus;
    private Runnable confirmable;
    private boolean confirmTimedOut = true;
    
    static {
        SERVER = new SettlementPlayer(SERVER_NAME);
    }
    
    public SettlementPlayer(Player plr) {
        player = plr;
        name = plr.getName();
    }
    
    
    private SettlementPlayer(String SERVER) {
        if (!SERVER.equals(SERVER_NAME)) {
            name = null;
            player = null;
            return;
        }
        name = SERVER;
        player = null;
    }
    
    
    public static SettlementPlayer getSettlementPlayer(Player plr) {
        if (playerMap.containsKey(plr))
            return SettlementPlayer.playerMap.get(plr);
        SettlementPlayer sp = new SettlementPlayer(plr);
        addNewPlayer(plr, sp);
        return sp;
    }
    
    
    public static void loadPlayer(SettlementPlayer player) {
        playerMap.put(Bukkit.getPlayer(player.getName()), player);
        if (player.isSettlementMember()) {
            for (Settlement s : player.getSettlements()) {
                s.addMember(player);
            }
        }
    }
    
    public void register() {
        loadFromFile();
        playerMap.put(player, this);
        for (SettlementData data : settlementsInfo) {
            data.getSettlement().addOnlinePlayer(this);
        }
    }
    
    public void unregister() {
        saveToFile();
        playerMap.remove(player);
        for (SettlementData data : settlementsInfo) {
            data.getSettlement().removeOnlinePlayer(this);
        }
    }
    
    protected void loadFromFile() {
        
    }
    
    protected void saveToFile() {
        
    }
    
    /**
     * Registers a new player who has logged in on the first time.
     * 
     * @param player Player to be added
     * 
     * @param sPlayer SettlementPlayer object to save as.
     * */
    public static void addNewPlayer(Player player, SettlementPlayer sPlayer) {
        playerMap.put(player, sPlayer);
        newPlayers.add(sPlayer);
    }

    

    @Override
    public void readExternal(ObjectInput arg0) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        // TODO Auto-generated method stub

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
        return 31 * player.hashCode();
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public String getName() {
        return name;
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
    
    public SettlementRank getRank(String settlementName) {
        for (SettlementData data : settlementsInfo) {
            if (data.getSettlementName().equalsIgnoreCase(settlementName)) {
                return data.getRank();
            }
        }
        return null;
    }
    
    class InvalidServerSettlementException extends Exception {
        private static final long serialVersionUID = -5812285711090480100L;
        
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
    public SettlementData getData(Settlement settlement) {
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
     * @param rank SettlementRank to assign to player.
     * */
    public void setRank(Settlement settlement, SettlementRank rank) {
        if (getData(settlement) != null) {
            getData(settlement).setRank(rank);
        }
    }
    
    public SettlementRank getRank(Settlement settlement) {
        return getData(settlement) != null ? getData(settlement).getRank() : null;
    }
    
    public void setTitle (Settlement settlement, String title) {
        getData(settlement).setTitle(title);
    }


    public Settlement getFocus() {
        if (settlementsInfo.size() == 1) {
            SettlementData singleSet = settlementsInfo.iterator().next();
            focus = singleSet.getSettlement();
        }
        return focus;
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
}
