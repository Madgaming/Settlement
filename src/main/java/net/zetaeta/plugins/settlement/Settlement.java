package net.zetaeta.plugins.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;

public class Settlement implements Externalizable {

	@ToBeSaved
	private int bonusPlots;
	@ToBeSaved
	private String name;
	@ToBeSaved
	private String description;
	@ToBeSaved
	private SettlementLocation spawn;
	@ToBeSaved
	private String ownerName;
	@ToBeSaved
	private List<Chunk> plots = new ArrayList<Chunk>();
	
	private SettlementPlayer owner;
	private Set<SettlementPlayer> members = new HashSet<SettlementPlayer>();
	
	public static Set<Settlement> allSettlements = new HashSet<Settlement>();
	
	
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
	}

	public static void loadSettlement(Settlement settlement) {
		allSettlements.add(settlement);
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
	public String getName() { return name; }

	/**
	 * @param name the name to set
	 */
	public void setName(String name) { this.name = name;}
	
	/**
	 * @return Settlement's owner.
	 * */
	public SettlementPlayer getOwner() { return owner; }
	
	/**
	 * @param New owner to assign to settlement.
	 * */
	public void setOwner(SettlementPlayer newOwner) { owner = newOwner; }

	/**
	 * Called to delete the settlement, clearing up memory and files.
	 * */
	public void delete() {
		// TODO: Complete method.
	}
	
	public void addMember(SettlementPlayer newMember) {
		members.add(newMember);
		SettlementData data = new SettlementData();
		data.setRank(SettlementRank.MEMBER);
		data.setSettlement(this);
		data.setTitle("");
		newMember.addData(data);
	}

}
