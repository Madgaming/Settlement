package net.zetaeta.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class Settlement implements Externalizable {

    private static Map<String, Settlement> allSettlements = new HashMap<String, Settlement>();
    
    @ToBeSaved
    private int bonusPlots;
    @ToBeSaved
    private String name;
    @ToBeSaved
    private String slogan;
    @ToBeSaved
    private SettlementLocation spawn;
    @ToBeSaved
    private String ownerName;
    @ToBeSaved
    private Collection<Chunk> plots = new HashSet<Chunk>();
    @ToBeSaved
    private Collection<String> invitations = new HashSet<String>(); // Names of players invited to the faction.
    private SettlementPlayer owner;
    private Set<SettlementPlayer> onlineMembers = new HashSet<SettlementPlayer>();
    private Set<String> members = new HashSet<String>();
    private boolean shouldSave = true; // Only set to false if Settlement is deleted.
    private int allowedPlots;
    
    /**
     * Standard settlement constructor
     * 
     * @param owner Owner to assign to settlement
     * 
     * @param name Name of settlement
     * */
    public Settlement(SettlementPlayer owner, String name) {
        this.name = name;
        this.ownerName = owner.getName();
        this.owner = owner;
        allSettlements.put(name, this);
    }

    public static Settlement getSettlement(String name) {
        return allSettlements.get(name);
    }
    
    public static Collection<Settlement> getSettlements() {
        return allSettlements.values();
    }
    
    public static void loadSettlement(Settlement settlement) {
        allSettlements.put(settlement.name, settlement);
    }
    
    /**
     * {@inheritDoc}
     * */
    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }
    
    /**
     * @return settlement's name
     */
    public String getName() { 
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) { 
        this.name = name;
    }
    
    /**
     * Sets the Settlement's slogan, shown on its info page and whenever someone enters its territory.
     * 
     * @param slogan New slogan to be set.
     */
    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
    /**
     * Gets the Settlement's slogan, shown on its info page and whenever someone enters its territory.
     * 
     * @return Settlement's current slogan.
     */
    public String getSlogan() {
        return slogan;
    }
    
    /**
     * @return Settlement's owner.
     * */
    public SettlementPlayer getOwner() { 
        return owner;
    }
    
    /**
     * @param newOwner New owner to assign to settlement.
     * */
    public void setOwner(SettlementPlayer newOwner) { 
        owner = newOwner;
    }

    /**
     * Adds a player to the Settlement's internal list of who's online.
     * 
     * @param sPlayer SettlementPlayer to add.
     */
    public void addOnlinePlayer(SettlementPlayer sPlayer) {
        if (members.contains(sPlayer.getName())) {
            onlineMembers.add(sPlayer);
        }
    }
    
    /**
     * Removes a player from the Settlement's internal list of who's online.
     * 
     * @param sPlayer SettlemenPlayer to remove.
     */
    public void removeOnlinePlayer(SettlementPlayer sPlayer) {
        if (onlineMembers.contains(sPlayer)) {
            onlineMembers.remove(sPlayer);
        }
    }
    
    /**
     * Checks if the settlement owns a specific chunk.
     * 
     * @param chunk {@link Chunk} Chunk to check ownership of.
     * @return true if the Settlements owns the Chunk, false otherwise.
     */
    public boolean ownsChunk(Chunk chunk) {
        return plots.contains(chunk);
    }
    
    public void broadcastSettlementMessage(String... message) {
        for (SettlementPlayer sPlayer : onlineMembers) {
            sendMessage(sPlayer, message);
        }
    }
    
    @SuppressWarnings("static-access")
    public void sendMessage(SettlementPlayer sPlayer, String... message) {
        Player player = sPlayer.getPlayer();
        player.sendMessage(SettlementUtil.concatString("§2========:§6", name,"§2:========"));
        player.sendMessage(message);
        player.sendMessage(SettlementMessenger.SETTLEMENT_MESSAGE_END);
    }
    
    public void sendNoRightsMessage(SettlementPlayer player) {
        sendMessage(player, "§cYou do not have sufficient rights to do this!");
    }
    
    /**
     * Called to delete the settlement, clearing up memory and files.
     * */
    public void delete() {
        allSettlements.remove(name);
        for (SettlementPlayer sPlayer : onlineMembers) {
            sPlayer.removeSettlement(this);
        }
        
    }
    
    public void addMember(SettlementPlayer newMember) {
        onlineMembers.add(newMember);
        SettlementData data = new SettlementData(this, SettlementRank.MEMBER);
        newMember.addData(data);
    }

    public void addInvitation(String player) {
        invitations.add(player);
    }
    
    
    @SuppressWarnings("static-access")
    public boolean claimLand(Player cause) {
        Chunk chunk = cause.getLocation().getChunk();
        Settlement prevOwner = SettlementUtil.getOwner(chunk);
        if (prevOwner == this) {
            SettlementMessenger.sendSettlementMessage(cause, "§a  This plot already belongs to you!");
            return false;
        }
        if (prevOwner != null) {
            SettlementMessenger.sendSettlementMessage(cause, "§c  This plot already belongs to someone else!");
            return false;
        }
        if (plots.size() >= allowedPlots) {
            SettlementMessenger.sendSettlementMessage(cause, "§c  The Settlement has used up its available claims already!");
            return false;
        }
        plots.add(chunk);
//        SettlementUtil.sendSettlementMessage(cause, SettlementUtil.concatString("§a  You claimed land at X:", chunk.getX(), ", Z:", chunk.getZ(), " for the Settlement ", name));
        broadcastSettlementMessage(SettlementUtil.concatString("§a  ", cause.getName(), " claimed land at X:", chunk.getX(), ", Z:", chunk.getZ()));
        return true;
    }
    
    
    public Collection<String> getInvitations() {
        return invitations;
    }
    
    public boolean isInvited(SettlementPlayer sPlayer) {
        return invitations.contains(sPlayer.getName());
    }
    
    public boolean isInvited(String spName) {
        return invitations.contains(spName);
    }
}
