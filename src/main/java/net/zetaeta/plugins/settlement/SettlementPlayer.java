package net.zetaeta.plugins.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import net.zetaeta.plugins.settlement.permissions.SettlementPermission;

import org.bukkit.entity.Player;

public class SettlementPlayer implements Externalizable {

	@ToBeSaved
	private List<SettlementPermission> permissions;
	@ToBeSaved
	private String name;
	private Player player;

	public SettlementPlayer(Player plr) {
		player = plr;
		name = plr.getName();
	}

	public static SettlementPlayer getSettlementPlayer(Player plr) {
		if (Databases.allPlayers.containsKey(plr))
			return Databases.allPlayers.get(plr);
		else {
			SettlementPlayer sp = new SettlementPlayer(plr);
			Databases.addNewPlayer(plr, sp);
			return sp;
		}
	}

	@Override
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {

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

}
