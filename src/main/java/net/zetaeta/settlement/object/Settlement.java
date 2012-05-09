package net.zetaeta.settlement.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.zetaeta.libraries.util.StringUtil;
import net.zetaeta.settlement.ConfigurationConstants;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.ToBeSaved;
import net.zetaeta.settlement.commands.settlement.Info;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a settlement, a group of people with an owner and moderators.
 * <p />
 * If you haven't noticed, I am not very good at writing comprehensive javadocs.
 * 
 * @author Zetaeta
 *
 */
public class Settlement implements SettlementConstants, Comparable<Settlement> {

//    private static Map<World, Collection<Settlement>> settlementsByWorld;
    
    public static final Settlement WILDERNESS;
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
    Map<World, Collection<Chunk>> worldPlots;// = new HashMap<World, Collection<Chunk>>();
    @ToBeSaved
    private Collection<String> invitations = new HashSet<String>(); // Names of players invited to the faction.
    private SettlementPlayer owner;
    private Set<SettlementPlayer> onlineMembers = new HashSet<SettlementPlayer>();
    private Set<String> members = new TreeSet<String>();
    private boolean shouldSave = true; // Only set to false if Settlement is deleted.
    private int allowedPlots;
    private String playerListCache;
    private boolean updatePLCache = true;
    
    static {
        WILDERNESS = new Settlement("Wilderness");
    }
    
    public Settlement(String name) {
        owner = SettlementPlayer.NONE;
        this.UID = 0;
        this.name = name;
    }
    
    /**
     * Standard settlement constructor
     * 
     * @param owner Owner to assign to settlement. 
     * @param name Name of settlement.
     * @param UID Unique ID for the Settlement.
     */
    public Settlement(SettlementPlayer owner, String name, int UID) {
        this.name = name;
        slogan = "§e  Use /settlement set slogan <slogan> to set the slogan!";
        this.ownerName = owner.getName();
        this.owner = owner;
        members.add(ownerName);
        onlineMembers.add(owner);
        this.UID = UID;
//        settlementsByName.put(name, this);
//        settlementsByUID.put(UID, this);
        allowedPlots = ConfigurationConstants.plotsPerPlayer;
//        server.registerSettlement(this);
//        if (ConfigurationConstants.useSettlementWorldCacheing && settlementsByWorld == null) {
//            settlementsByWorld = new HashMap<World, Collection<Settlement>>();
//        }
    }
    
    /**
     * @param name Settlement name.
     * @param uid Unique ID for the settlement.
     */
    public Settlement(String name, int uid) {
        this.name = name;
        this.UID = uid;
        slogan = "§e  Use /settlement slogan <slogan> to set the slogan!";
//        server.registerSettlement(this);
//        settlementsByName.put(name, this);
//        settlementsByUID.put(UID, this);
//        if (ConfigurationConstants.useSettlementWorldCacheing && settlementsByWorld == null) {
//            settlementsByWorld = new HashMap<World, Collection<Settlement>>();
//        }
    }

    /**
     * Gets the Settlement's unique ID, which is guaranteed to be both unique to this settlement among all on the server and also be permanent,
     * unlike its the name which can be changed.
     * 
     * @return Settlement's UID
     */
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
     */
    public SettlementPlayer getOwner() { 
        return owner;
    }
    
    /**
     * @param newOwner New owner to assign to settlement.
     */
    public void setOwner(SettlementPlayer newOwner) { 
        owner = newOwner;
        setOwnerName(newOwner.getName());
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Gets the number of plots currently owned by the Settlement.
     * 
     * @return Number of plots owned.
     */
    public int getPlotCount() {
        return plots.size();
    }
    
    /**
     * Gets the maximum number of plots this Settlement can currently own.
     * <p />
     * This is calculated by <code><bonus plot granted> + (<number of members> * {@link ConfigurationConstants#plotsPerPlayer <plots per player>})(</code>
     * It is possible for a Settlement to have more plots than this, by claiming land while having more members, some of whom then leave.
     *
     * @return maximum allowed plots.
     */
    public int getPlotLimit() {
        return allowedPlots;
    }
    
    /**
     * Gets the number of bonus plots claims given to the settlement.
     *
     * @return Bonus plots granted to the settlement.
     */
    public int getBonusPlots() {
        return bonusPlots;
    }

    /**
     * Sets the number of bonus plots claims given to the settlement.
     *
     * @param bonusPlots the number bonus plots to grant
     */
    public void setBonusPlots(int bonusPlots) {
        this.bonusPlots = bonusPlots;
    }

    /**
     * Gets the number of members currently online.
     * 
     * @return Number of members currently online.
     */
    public int getOnlineMemberCount() {
        return onlineMembers.size();
    }
    
    /**
     * Gets the total number of members, online and offline.
     * 
     * @return Total member count.
     */
    public int getMemberCount() {
        return members.size();
    }
    
    /**
     * Gets a {@link Collection} of all {@link Chunk Chunks} currently belonging to the Settlement.
     * 
     * @return Chunks owned by the Settlement.
     */
    public Collection<Chunk> getPlots() {
        return plots;
    }
    
    /**
     * @return the spawn
     */
    public Location getSpawn() {
        return spawn;
    }

    /**
     * @param spawn the spawn to set
     */
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    /**
     * Gets a {@link Collection} of {@link SettlementPlayer SettlementPlayers} representing all currently online members.
     * 
     * @return All online members.
     */
    public Collection<SettlementPlayer> getOnlineMembers() {
        return onlineMembers;
    }
    
    /**
     * Gets a {@link Collection} of the usernames of all the Settlement's moderators.
     * 
     * @return Moderators' names.
     */
    public Collection<String> getModeratorNames() {
        return moderators;
    }
    
    /**
     * Gets a {@link Collection} of the usernames of all the Settlement's members.
     * 
     * @return Members' names.
     */
    public Collection<String> getMemberNames() {
        return members;
    }
    
    public Collection<String> getBaseMemberNames() {
        return baseMembers;
    }

    public void addModerator(String name) {
        moderators.add(name);
    }
    
    public void addModerator(SettlementPlayer sPlayer) {
        baseMembers.remove(sPlayer.getName());
        moderators.add(sPlayer.getName());
        sPlayer.setRank(this, Rank.MODERATOR);
    }
    
    public void removeModerator(SettlementPlayer sPlayer) {
        moderators.remove(sPlayer.getName());
        baseMembers.add(sPlayer.getName());
        sPlayer.setRank(this, Rank.MEMBER);
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
    
    /**
     * Changes the name of the Settlement, broadcasting a message to Settlement members that the name has been changed.
     * 
     * @param newName New name to assign to the Settlement.
     * @param cause {@link SettlementPlayer} that changed the name, to include in the broadcasted message.
     */
    public void changeName(String newName, SettlementPlayer cause) {
        server.changeSettlementName(this, newName);
        name = newName;
        broadcastSettlementMessage("§b  " + cause.getName() + " §achanged the settlement's name to " + name);
    }
    
    /**
     * Broadcasts a message using {@link #sendMessage(CommandSender, String...)} to all currently online members.
     * 
     * @param message Message to send in {@link String} or String[] form.
     */
    public void broadcastSettlementMessage(String... message) {
        for (SettlementPlayer sPlayer : onlineMembers) {
            sendMessage(sPlayer.getPlayer(), message);
        }
    }
    
    /**
     * Sends a message to the specified {@link CommandSender} "from the settlement", i.e titled with the settlement's name.
     *
     * @param target CommandSender to send the message to.
     * @param message Message to send in {@link String} or String[] form.
     */
    @SuppressWarnings("static-access")
    public void sendMessage(CommandSender target, String... message) {
        System.out.println(name);
        target.sendMessage(StringUtil.concatString(40, SettlementMessenger.SETTLEMENT_MESSAGE_START_LEFT + " §6", name, " ", SettlementMessenger.SETTLEMENT_MESSAGE_START_RIGHT));
        target.sendMessage(message);
        target.sendMessage(SettlementMessenger.SETTLEMENT_MESSAGE_END);
    }
    
    /**
     * Sends a standard message to the specified {@link CommandSender} telling them they do not have the rights to perform an action.
     * <p />
     * Used for internal Settlement modification situations, such as when a member tries to change the Settlement name.
     *
     * @param target CommandSender to send the message to.
     */
    public void sendNoRightsMessage(CommandSender target) {
        sendMessage(target, "§cYou do not have sufficient rights to do this!");
    }
    
    /**
     * Sends the info message about the Settlement to the specified {@link CommandSender}.
     * <p />
     * Used for the {@link Info /settlement info} command.
     * 
     * @param target CommandSender to send the message to.
     */
    @SuppressWarnings("static-access")
    public void sendInfoMessage(CommandSender target) {
        List<String> info = new ArrayList<String>();
        info.add("§2 - §a".concat(slogan == null ? "null" : slogan));
        info.add("§2 Owner: ".concat(owner == null ? (ownerName == null ? "owner&name = null" : ownerName) : (owner.getPlayer() == null ? "ownerPlayer = null" : owner.getPlayer().getDisplayName()) ));
        if (moderators.size() > 0) {
            info.add(StringUtil.concatString((moderators.size() << 4) + 18, "§2 Moderators: ", StringUtil.arrayAsCommaString(moderators.toArray(new String[moderators.size()]))));
        }
        info.add("§2  Plots: " + plots.size() + '/' + allowedPlots);
        info.add(getColouredPlayerList("§a", "§c"));
        sendMessage(target, info.toArray(new String[info.size()]));
    }
    
    /**
     * Gets a coloured String list of all players in the Settlement, with online players coloured separately to offline ones.
     * <p />
     * Used, for example, in {@link #sendInfoMessage(CommandSender)} in the form <code>getColouredPlayerList("§a", "§c")</code>.
     * 
     * @param onlineColour Colour to colour online players' names with, in standard Minecraft string form using "§".
     * @param offlineColour Colour to colour offline players' names with, in standard Minecraft string form using "§".
     * 
     * @return Coloured player list as a String.
     */
    @SuppressWarnings("static-access")
    public String getColouredPlayerList(String onlineColour, String offlineColour) {
        if (updatePLCache || playerListCache == null) {
            String[] plArray = new String[members.size()];
            int i = 0;
            for (Iterator<String> listIt = members.iterator(); listIt.hasNext(); ++i) {
                String name = listIt.next();
                if (server.getSettlementPlayer(name) != null) {
                    plArray[i] = onlineColour + name;
                }
                else {
                    plArray[i] = offlineColour + name;
                }
            }
            return StringUtil.concatString(15 + (members.size() * 18), "§2 Players: ", StringUtil.arrayAsCommaString(plArray), ".");
        }
        return playerListCache;
    }

    /**
     * Called to delete the settlement, clearing up memory and files.
     * */
    public void delete() {
        server.unregisterSettlement(this);
//        if (settlementsByWorld != null) {
//            settlementsByWorld.remove(name);
//        }
        for (SettlementPlayer sPlayer : onlineMembers) {
            sPlayer.removeSettlement(this);
        }
        for (SettlementPlayer sPlayer : server.getOnlinePlayers()) {
            if (sPlayer.getFocus() == this) {
                sPlayer.setFocus(null);
            }
        }
        server.clearFromChunkCache(this);
    }
    
    /**
     * Updates the current allowed number of plots for the Settlement to own.
     * <p />
     * This is calculated by <code>{@literal <bonus plot granted> + (<number of members> * }{@link ConfigurationConstants#plotsPerPlayer})</code>
     */
    public void updateClaimablePlots() {
        allowedPlots = bonusPlots + (members.size() * ConfigurationConstants.plotsPerPlayer);
    }
    
    /**
     * Adds a member to the Settlement, updating the number of claimable plots aswell.
     *
     * @param newMember SettlementPlayer to add.
     */
    public void addMember(SettlementPlayer newMember) {
        onlineMembers.add(newMember);
        baseMembers.add(newMember.getName());
        members.add(newMember.getName());
//        if (newMember.getData(this) == null) {
//            SettlementData data = new SettlementData(this, Rank.MEMBER);
//            newMember.addData(data);
//        }
        newMember.setRank(this, Rank.MEMBER);
        updateClaimablePlots();
    }
    
    public void addMember(String newMemberName) {
        baseMembers.add(newMemberName);
        members.add(newMemberName);
        if (server.getSettlementPlayer(newMemberName) != null) {
            onlineMembers.add(server.getSettlementPlayer(newMemberName));
        }
    }
    
    /**
     * Checks whether the given {@link SettlementPlayer} is a member of the Settlement.
     *
     * @param sPlayer SettlementPlayer to check membership of.
     * @return <code>true</code> if <code>sPlayer</code> is a member of the Settlement, <code>false</code> otherwise.
     */
    public boolean isMember(SettlementPlayer sPlayer) {
        return onlineMembers.contains(sPlayer);
    }
    
    public SettlementPlayer getMember(String name) {
        String lowerName = name.toLowerCase();
        SettlementPlayer result = null;
        int length = Integer.MAX_VALUE;
        for (SettlementPlayer poss : onlineMembers) {
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
     * Checks whether the online or offline player with the given name is a member of the Settlement.
     *
     * @param sPlayerName Name of the player to check membership.
     * @return Whether the player is a member of the Settlement.
     */
    public boolean isMember(String sPlayerName) {
        return members.contains(sPlayerName);
    }
    
    public boolean isModerator(SettlementPlayer sPlayer) {
        return moderators.contains(sPlayer.getName());
    }
    
    public boolean isModerator(String sPlayerName) {
        return moderators.contains(sPlayerName);
    }
    
    /**
     * Removes an online member from the Settlement.
     *
     * @param oldMember SettlementPlayer to remove.
     */
    public void removeMember(SettlementPlayer oldMember) {
        onlineMembers.remove(oldMember);
        oldMember.removeSettlement(this);
        updateClaimablePlots();
    }
    
    public void removeMember(String oldMemberName) {
        SettlementPlayer sp;
        if ((sp = server.getSettlementPlayer(oldMemberName)) != null) {
            removeMember(sp);
        }
        else {
            members.remove(oldMemberName);
            baseMembers.remove(oldMemberName);
            moderators.remove(oldMemberName);
        }
    }
    
    /**
     * Updates the members in the overall member list.
     * <p />
     * As the owner's name, moderators' names and members' names are stored separately in the file,
     * this must be called after loading to update the contents of the overall member list.
     */
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
        Settlement prevOwner = server.getOwner(chunk);
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
        broadcastSettlementMessage(StringUtil.concatString("§a  ", cause.getName(), " claimed land at X:", chunk.getX(), ", Z:", chunk.getZ()));
        return true;
    }
    
    @SuppressWarnings("static-access")
    public boolean unclaimLand(Player cause) {
        Chunk chunk = cause.getLocation().getChunk();
        Settlement prevOwner = server.getOwner(chunk);
        if (!this.equals(prevOwner)) {
            SettlementMessenger.sendSettlementMessage(cause, "§a  This plot does not belong to you!");
            return false;
        }
        plots.remove(chunk);
        server.clearFromChunkCache(chunk);
        broadcastSettlementMessage(StringUtil.concatString("§a  ", cause.getName(), " unclaimed land at X:", chunk.getX(), ", Z:", chunk.getZ()));
        return true;
    }
    
    public boolean addChunk(Chunk chunk) {
        plots.add(chunk);
        if (ConfigurationConstants.useSettlementWorldCacheing) {
        }
        return true;
    }
    
    private Collection<String> getInvitations() {
        return invitations;
    }
    
    public boolean isInvited(SettlementPlayer sPlayer) {
        return invitations.contains(sPlayer.getName());
    }
    
    public boolean isInvited(String spName) {
        return invitations.contains(spName);
    }
    
    public String toString() {
        return "All members: " + members + "; Base members: " + baseMembers + "; Moderators: " + moderators + "; Owner: " + ownerName;
    }

    @Override
    public int compareTo(Settlement other) {
        return this.members.size() == other.members.size() ? 0 : (this.members.size() > other.members.size() ? 1 : -1);
    }
}
