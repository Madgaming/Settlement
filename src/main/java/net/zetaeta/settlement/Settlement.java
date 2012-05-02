package net.zetaeta.settlement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Settlement implements Externalizable, SettlementConstants {

    private static Map<String, Settlement> settlementsByName = new HashMap<String, Settlement>();
    private static Map<Integer, Settlement> settlementsByUID = new HashMap<Integer, Settlement>();
    private static Map<World, Collection<Settlement>> settlementsByWorld;
    
    @ToBeSaved
    private int bonusPlots;
    @ToBeSaved
    private int UID;
    @ToBeSaved
    private String name;
    @ToBeSaved
    private String slogan;
    @ToBeSaved
    private Location spawn;
    @ToBeSaved
    private String ownerName;
    @ToBeSaved
    private Set<String> moderators = new HashSet<String>();
    @ToBeSaved
    private Set<String> baseMembers = new HashSet<String>();
    @ToBeSaved
    private Collection<Chunk> plots = new HashSet<Chunk>();
    private Map<World, Collection<Chunk>> worldPlots;// = new HashMap<World, Collection<Chunk>>();
    @ToBeSaved
    private Collection<String> invitations = new HashSet<String>(); // Names of players invited to the faction.
    private SettlementPlayer owner;
    private Set<SettlementPlayer> onlineMembers = new HashSet<SettlementPlayer>();
    private Set<String> members = new TreeSet<String>();
    private boolean shouldSave = true; // Only set to false if Settlement is deleted.
    private int allowedPlots;
    private String playerListCache;
    private boolean updatePLCache = true;
    
    /**
     * Standard settlement constructor
     * 
     * @param owner Owner to assign to settlement
     * 
     * @param name Name of settlement
     * @param UID Unique ID for the Settlement.
     * */
    public Settlement(SettlementPlayer owner, String name, int UID) {
        SettlementPlugin.log.info("Settlement(), name = " + name + ", UID = " + UID);
        this.name = name;
        slogan = "§e  Use /settlement slogan <slogan> to set the slogan!";
        this.ownerName = owner.getName();
        this.owner = owner;
        members.add(ownerName);
        onlineMembers.add(owner);
        this.UID = UID;
        SettlementPlugin.log.info("InitialisedVars");
        settlementsByName.put(name, this);
        settlementsByUID.put(UID, this);
        SettlementPlugin.log.info("MapsAdded");
        SettlementPlugin.log.info(this.name);
        if (ConfigurationConstants.useSettlementWorldCacheing && settlementsByWorld == null) {
            settlementsByWorld = new HashMap<World, Collection<Settlement>>();
        }
    }
    
    public Settlement(String name, int uid) {
        this.name = name;
        this.UID = uid;
        slogan = "§e  Use /settlement slogan <slogan> to set the slogan!";
        settlementsByName.put(name, this);
        settlementsByUID.put(UID, this);
        if (ConfigurationConstants.useSettlementWorldCacheing && settlementsByWorld == null) {
            settlementsByWorld = new HashMap<World, Collection<Settlement>>();
        }
    }

    public static Settlement getSettlement(String name) {
        return settlementsByName.get(name);
    }
    
    public static Settlement getSettlement(int uid) {
        return settlementsByUID.get(uid);
    }
    
    public static Collection<Settlement> getSettlements() {
        return settlementsByName.values();
    }
    
    public static Collection<Settlement> getSettlementsIn(World world) {
        return settlementsByWorld.get(world);
    }
    
    public static void loadSettlement(Settlement settlement) {
        settlementsByName.put(settlement.name, settlement);
    }
    
    public static void loadSettlements() {
        log.info("loadSettlements");
        File settlementsFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
            try {
                settlementsFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create settlements.dat file!", e);
                e.printStackTrace();
            }
            return;
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(settlementsFile));
        } catch (FileNotFoundException e) {
            log.severe("Could not open settlements.dat file!");
            return;
        }
        
        
        try {
            if (dis.available() > 0) {
                int version = dis.readInt();
                if (version == 0) {
                    while(dis.available() > 0) {
                        FlatFileIO.loadSettlementV0_0(dis);
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
    }
    
    public static void saveSettlements() {
        log.info("saveSettlements");
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
        }
        try {
            dos.writeInt(FlatFileIO.FILE_FORMAT_VERSION);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        for (Settlement settlement : settlementsByUID.values()) {
            try {
                FlatFileIO.saveSettlementV0_0(settlement, dos);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error while saving Settlement " + settlement.name + "!", e);
                e.printStackTrace();
            }
        }
    }
    
    public static int getNewUID() {
        System.out.println("getNewUID");
        Random uidGen = new Random();
        int possible = 0;
        do {
            possible = uidGen.nextInt();
        }
        while (settlementsByUID.containsKey(possible));
        return possible;
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
    
    public int getUid() {
        return UID;
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
        ownerName = newOwner.getName();
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
        if (sPlayer == owner) {
            owner = null;
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
            sendMessage(sPlayer.getPlayer(), message);
        }
    }
    
    @SuppressWarnings("static-access")
    public void sendMessage(CommandSender target, String... message) {
        System.out.println(name);
        target.sendMessage(SettlementUtil.concatString(40, SettlementMessenger.SETTLEMENT_MESSAGE_START_LEFT + " §6", name, " ", SettlementMessenger.SETTLEMENT_MESSAGE_START_RIGHT));
        target.sendMessage(message);
        target.sendMessage(SettlementMessenger.SETTLEMENT_MESSAGE_END);
    }
    
    public void sendNoRightsMessage(CommandSender target) {
        sendMessage(target, "§cYou do not have sufficient rights to do this!");
    }
    
    @SuppressWarnings("static-access")
    public void sendInfoMessage(CommandSender target) {
        List<String> info = new ArrayList<String>();
        info.add("§2 - ".concat(slogan == null ? "null" : slogan));
        info.add("§2 Owner: ".concat(owner == null ? (ownerName == null ? "owner&name = null" : ownerName) : (owner.getPlayer() == null ? "ownerPlayer = null" : owner.getPlayer().getDisplayName()) ));
        if (moderators.size() > 0) {
            info.add(SettlementUtil.concatString((moderators.size() * 16) + 18, "§2 Moderators: ", SettlementUtil.arrayAsCommaString(moderators.toArray(new String[moderators.size()]))));
        }
        info.add(getColouredPlayerList());
        sendMessage(target, info.toArray(new String[info.size()]));
    }
    
    private String getColouredPlayerList() {
        if (updatePLCache || playerListCache == null) {
            String[] plArray = new String[members.size()];
            int i = 0;
            for (Iterator<String> listIt = members.iterator(); listIt.hasNext(); ++i) {
                String name = listIt.next();
                if (SettlementPlayer.getSettlementPlayer(name) != null) {
                    plArray[i] = "§a" + name;
                }
                else {
                    plArray[i] = "§c" + name;
                }
            }
            return SettlementUtil.concatString(15 + (members.size() * 18), "§2 Players: ", SettlementUtil.arrayAsCommaString(plArray), ".");
        }
        return playerListCache;
    }

    /**
     * Called to delete the settlement, clearing up memory and files.
     * */
    public void delete() {
        settlementsByName.remove(name);
        settlementsByUID.remove(name);
        if (settlementsByWorld != null) {
            settlementsByWorld.remove(name);
        }
        for (SettlementPlayer sPlayer : onlineMembers) {
            sPlayer.removeSettlement(this);
        }
        for (SettlementPlayer sPlayer : SettlementPlayer.getOnlinePlayers()) {
            if (sPlayer.getFocus() == this) {
                sPlayer.setFocus(null);
            }
        }
    }
    
    public void updateClaimablePlots() {
        allowedPlots = bonusPlots + (members.size() * ConfigurationConstants.plotsPerPlayer);
    }
    
    public void addMember(SettlementPlayer newMember) {
        onlineMembers.add(newMember);
        if (newMember.getData(this) == null) {
            SettlementData data = new SettlementData(this, SettlementRank.MEMBER);
            newMember.addData(data);
        }
        updateClaimablePlots();
    }
    
    public boolean isMember(SettlementPlayer sPlayer) {
        return onlineMembers.contains(sPlayer);
    }
    
    public boolean isMember(String sPlayerName) {
        return members.contains(sPlayerName);
    }
    
    public void removeMember(SettlementPlayer oldMember) {
        onlineMembers.remove(oldMember);
        oldMember.removeSettlement(this);
        updateClaimablePlots();
    }
    
    public void updateMembers() {
        for (String bName : baseMembers) {
            members.add(bName);
        }
        for (String mName : moderators) {
            members.add(mName);
        }
        members.add(ownerName);
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
            sendMessage(cause, "§c  This plot already belongs to someone else!");
            return false;
        }
        if (plots.size() >= allowedPlots) {
            sendMessage(cause, "§c  The Settlement has used up its available claims already!");
            return false;
        }
        plots.add(chunk);
        if (ConfigurationConstants.useSettlementWorldCacheing) {
            if (settlementsByWorld == null) {
                settlementsByWorld = new HashMap<World, Collection<Settlement>>();
            }
            if (settlementsByWorld.get(chunk.getWorld()) == null) {
                settlementsByWorld.put(chunk.getWorld(), new HashSet<Settlement>());
            }
            settlementsByWorld.get(chunk.getWorld()).add(this);
        }
        broadcastSettlementMessage(SettlementUtil.concatString("§a  ", cause.getName(), " claimed land at X:", chunk.getX(), ", Z:", chunk.getZ()));
        return true;
    }
    
    public boolean addChunk(Chunk chunk) {
        plots.add(chunk);
        if (ConfigurationConstants.useSettlementWorldCacheing) {
            if (ConfigurationConstants.useSettlementWorldCacheing) {
                if (settlementsByWorld == null) {
                    settlementsByWorld = new HashMap<World, Collection<Settlement>>();
                }
                if (settlementsByWorld.get(chunk.getWorld()) == null) {
                    settlementsByWorld.put(chunk.getWorld(), new HashSet<Settlement>());
                }
                settlementsByWorld.get(chunk.getWorld()).add(this);
            }
        }
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
    
    public static class FlatFileIO {
        
        static Map<World, Collection<Chunk>> reusableWorldPlots;
        
        public static final int FILE_FORMAT_VERSION = 0;
        
        public static void saveSettlementV0_0(Settlement set, DataOutputStream dos) throws IOException {
            System.out.println("saveSettlementV0_0: " + set.getName());
            log.info("UID: " + set.UID);
            dos.writeInt(set.UID);
            dos.writeUTF(set.name);
            dos.writeUTF(set.slogan);
            dos.writeInt(set.bonusPlots);
            if (set.spawn != null) {
                dos.writeChar('[');
                UUID sWorldUid = set.spawn.getWorld().getUID();
                dos.writeLong(sWorldUid.getMostSignificantBits());
                dos.writeLong(sWorldUid.getLeastSignificantBits());
                dos.writeInt(set.spawn.getBlockX());
                dos.writeInt(set.spawn.getBlockY());
                dos.writeInt(set.spawn.getBlockZ());
                dos.writeFloat(set.spawn.getYaw());
                dos.writeFloat(set.spawn.getPitch());
                dos.writeChar(']');
            }
            else {
                dos.writeChar('|');
            }
            dos.writeUTF(set.ownerName);
            if (set.moderators.size() > 0) {
                dos.writeChar('{');
                for (String s : set.moderators) {
                    dos.writeUTF(s);
                    dos.writeChar(',');
                }
                dos.writeChar('}');
            }
            else {
                dos.writeChar('|');
            }
            if (set.baseMembers.size() > 0) {
                dos.writeChar('{');
                for (String s : set.baseMembers) {
                    dos.writeUTF(s);
                    dos.writeChar(',');
                }
                dos.writeChar('}');
            }
            else {
                dos.writeChar('|');
            }
            if (ConfigurationConstants.useSettlementWorldCacheing && set.worldPlots != null) {
                if (set.worldPlots.size() > 0) {
                    dos.writeChar('{');
                    for (World wrld : set.worldPlots.keySet()) {
                        Collection<Chunk> chunks = set.worldPlots.get(wrld);
                        if (chunks.size() > 0) {
                            dos.writeChar('{');
                            dos.writeLong(wrld.getUID().getMostSignificantBits());
                            dos.writeLong(wrld.getUID().getLeastSignificantBits());
                            dos.writeChar(':');
                            for (Chunk ch : chunks) {
                                dos.writeChar(',');
                                dos.writeInt(ch.getX());
                                dos.writeInt(ch.getZ());
                            }
                            dos.writeChar('}');
                            dos.writeChar(';');
                        }
                        else {
                            dos.writeChar('|');
                        }
                    }
                    dos.writeChar('}');
                }
                else {
                    dos.writeChar('|');
                }
            } else {
                if (set.plots.size() > 0) {
                    if (reusableWorldPlots == null) {
                        reusableWorldPlots = new HashMap<World, Collection<Chunk>>();
                    }
                    for (Chunk ch : set.plots) {
                        if (reusableWorldPlots.get(ch.getWorld()) == null) {
                            reusableWorldPlots.put(ch.getWorld(), new HashSet<Chunk>());
                        }
                        reusableWorldPlots.get(ch.getWorld()).add(ch);
                    }
                    dos.writeChar('{');
                    for (World wrld : reusableWorldPlots.keySet()) {
                        Collection<Chunk> chunks = reusableWorldPlots.get(wrld);
                        if (chunks.size() > 0) {
                            dos.writeChar('{');
                            dos.writeLong(wrld.getUID().getMostSignificantBits());
                            dos.writeLong(wrld.getUID().getLeastSignificantBits());
                            dos.writeChar(':');
                            for (Chunk ch : chunks) {
                                dos.writeChar(',');
                                dos.writeInt(ch.getX());
                                dos.writeInt(ch.getZ());
                            }
                            dos.writeChar('}');
                            dos.writeChar(';');
                        }
                        else {
                            dos.writeChar('|');
                        }
                    }
                    dos.writeChar('}');
                }
                else {
                    dos.writeChar('|');
                }
            }
                
            dos.writeChar('\n');
        }
        
        public static Settlement loadSettlementV0_0(DataInputStream dis) throws IOException {
            int uid = dis.readInt();
            log.info("UID: " + uid);
            String name = dis.readUTF();
            Settlement set = new Settlement(name, uid);
            set.slogan = dis.readUTF();
            set.bonusPlots = dis.readInt();
            
            // Spawn location
            if (dis.readChar() == '[') { // [
                long sUidStart = dis.readLong();
                long sUidEnd = dis.readLong();
                int x = dis.readInt();
                int y = dis.readInt();
                int z = dis.readInt();
                float yaw = dis.readFloat();
                float pitch = dis.readFloat();
                dis.readChar(); // ]
                UUID sWorldUid = new UUID(sUidStart, sUidEnd);
                World world = Bukkit.getWorld(sWorldUid);
                if (world != null) {
                    set.spawn = new Location(world, x, y, z, yaw, pitch);
                }
            }
            // done spawn
            
            String ownerName = dis.readUTF();
            set.ownerName = ownerName;
            
//            dis.readChar(); // {
            char c = dis.readChar();
            while (c != '|' && c != '}') {
                set.moderators.add(dis.readUTF());
                c = dis.readChar();
            } // }
//            dis.readChar(); // {
            c = dis.readChar();
            while (c != '|' && c != '}') {
                set.baseMembers.add(dis.readUTF());
                c = dis.readChar();
            } // }
            c = dis.readChar();
            while (c != '|' && c != '}') { // Worlds
                c = dis.readChar(); // { / |
                if (c == '|') {
                    continue;
                }
                long uidStart = dis.readLong();
                long uidEnd = dis.readLong();
                UUID worldUid = new UUID(uidStart, uidEnd);
                World currWorld = Bukkit.getWorld(worldUid);
                dis.readChar();
                c = dis.readChar();
                while (c != '}') {
                    int cx = dis.readInt();
                    int cz = dis.readInt();
                    Chunk chk = currWorld.getChunkAt(cx, cz);
                    set.addChunk(chk);
                    c = dis.readChar();
                }
                dis.readChar();
                c = dis.readChar();
            }
            dis.readChar(); // \n
            set.updateMembers();
            set.updateClaimablePlots();
            log.info("Loaded Settlement " + set.name);
            return set;
        }
    }
}
