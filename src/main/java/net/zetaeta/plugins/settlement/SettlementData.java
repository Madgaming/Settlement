package net.zetaeta.plugins.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SettlementData implements Externalizable {

	@ToBeSaved
	private String settlementName;
	@ToBeSaved
	private SettlementRank rank;
	@ToBeSaved
	private String title;
	
	private Settlement settlement;
	
	
	/**
	 * Creates a SettlementData object with no data associated. 
	 * When this constructor is used, a settlement and rank should be associated using setSettlement() and setRank() as soon as possible.
	 * */
	public SettlementData() {
		this(null, SettlementRank.MEMBER, "");
	}
	
	/**
	 * Bare consructor, creates SettlementData with specified settlement and assigns default rank SettlementRank.MEMBER
	 * 
	 * @param settlement Settlement to associate with this data.
	 * */
	public SettlementData(Settlement settlement) {
		this(settlement, SettlementRank.MEMBER);
	}
	
	/**
	 * Standard constructor, creates SettlementData with necessary information. Should be used in most circumstances.
	 * 
	 * @param settlement Settlement to associate with this data.
	 * 
	 * @param rank SettlementRank to give to the player in this town.
	 * */
	public SettlementData(Settlement settlement, SettlementRank rank) {
		this(settlement, rank, "");
	}
	
	/**
	 * Full constructor, creates SettlementData and fills it with all information.
	 * 
	 * @param settlement Settlement this data is to be associated with.
	 * 
	 * @param rank SettlementRank to give to the player in this settlement.
	 * 
	 * @param title Title to give to the player in this settlement.
	 * */
	public SettlementData(Settlement settlement, SettlementRank rank, String title) {
		this.settlement = settlement;
		settlementName = settlement.getName();
		this.title = title;
		this.rank = rank;
	}
	
	
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Gets the player's rank in the settlement associated with this data.
	 * */
	public SettlementRank getRank() {
		return rank;
	}
	/**
	 * Convenience method, gets the name of the settlement associated with this data, should be slightly faster than <code>getSettlement().getName()</code>
	 * 
	 * @return Settlement name.
	 * */
	public String getSettlementName() {
		return settlementName;
	}
	/**
	 * Gets the player's title in the settlement associated with this data.
	 * 
	 * @return Associated title.
	 * */
	public String getTitle() {
		return title;
	}
	/**
	 * Gets the settlement associated with this data.
	 * 
	 * @return Associated settlement.
	 * */
	public Settlement getSettlement() {
		return settlement;
	}
	
	/**
	 * Set's the player's title in the settlement associated with this data.
	 * 
	 * @param title New title to associate.
	 * */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * Sets the player's SettlementRank in the settlement associated with this data.
	 * 
	 * @param rank New rank to associate.
	 * */
	public void setRank(SettlementRank rank) {
		this.rank = rank;
	}
	/**
	 * Sets the settlement associated with this data.
	 * 
	 * @param settlement New settlement to associate.
	 * */
	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
		this.settlementName = settlement.getName();
	}
}
