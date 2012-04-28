package net.zetaeta.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.zetaeta.libraries.SimpleInterHashMap;
import net.zetaeta.settlement.commands.SettlementPermission;

import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getPlayer;

public class SettlementPlayer implements Externalizable {

	public static final SettlementPlayer SERVER;
	public static SimpleInterHashMap<Player, SettlementPlayer> playerMap = new SimpleInterHashMap<Player, SettlementPlayer>();
	private static Set<SettlementPlayer> newPlayers = new HashSet<SettlementPlayer>();
	
//	@ToBeSaved
//	private List<SettlementPermission> permissions;
	@ToBeSaved
	private String name;
	@ToBeSaved
	private long lastOnline;
	@ToBeSaved
	private Set<SettlementData> settlementsInfo = new HashSet<SettlementData>();
	
	private Player player;
	private Settlement focus;
	private Runnable confirmable;
	
	static {
		SERVER = new SettlementPlayer("__%SERVER%__");
	}
	
	public SettlementPlayer(Player plr) {
		player = plr;
		name = plr.getName();
	}
	
	
	public SettlementPlayer(String SERVER) {
		if (!SERVER.equals("__%SERVER%__s")) {
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
		playerMap.putValue(player, getPlayer(player.getName()));
		if (player.isSettlementMember()) {
			for (Settlement s : player.getSettlements()) {
				s.addMember(player);
			}
		}
	}
	
	/**
	 * Registers a new player who has logged in on the first time.
	 * 
	 * @param plr Player to be added
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
	 * */
	public SettlementData getData(Settlement settlement) {
		for (SettlementData data : settlementsInfo) {
			if (data.getSettlement().equals(settlement)) {
				return data;
			}
		}
		return null;
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
		getData(settlement).setRank(rank);
	}
	
	public void setTitle (Settlement settlement, String title) {
		getData(settlement).setTitle(title);
	}


	public Settlement getFocus() {
		return focus;
	}

	public void setConfirmable(Runnable runnable) {
		confirmable = runnable;
	}
	
	public Runnable getConfirmable() {
		return confirmable;
	}

}
