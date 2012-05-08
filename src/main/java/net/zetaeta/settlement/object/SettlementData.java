package net.zetaeta.settlement.object;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.ToBeSaved;

public class SettlementData implements SettlementConstants {

    private String settlementName;
    @ToBeSaved
    private Rank rank;
    @ToBeSaved
    private String title;
    @ToBeSaved
    private int settlementUID;
    
    private Settlement settlement;
    
    /**
     * Creates a SettlementData object with no data associated. 
     * When this constructor is used, a settlement and rank should be associated using setSettlement() and setRank() as soon as possible.
     * @deprecated Use {@link #SettlementData(Settlement)} instead.
     */
    @Deprecated
    public SettlementData() {
        this(null, Rank.MEMBER, "");
    }
    
    /**
     * Bare consructor, creates SettlementData with specified settlement and assigns default rank Rank.MEMBER
     * 
     * @param settlement Settlement to associate with this data.
     * */
    public SettlementData(Settlement settlement) {
        this(settlement, Rank.MEMBER);
    }
    
    /**
     * Standard constructor, creates SettlementData with necessary information. Should be used in most circumstances.
     * 
     * @param settlement Settlement to associate with this data.
     * 
     * @param rank Rank to give to the player in this town.
     * */
    public SettlementData(Settlement settlement, Rank rank) {
        this(settlement, rank, "");
    }
    
    /**
     * Full constructor, creates SettlementData and fills it with all information.
     * 
     * @param settlement Settlement this data is to be associated with.
     * 
     * @param rank Rank to give to the player in this settlement.
     * 
     * @param title Title to give to the player in this settlement.
     * */
    public SettlementData(Settlement settlement, Rank rank, String title) {
        this.settlement = settlement;
        settlementName = settlement.getName();
        this.title = title;
        this.rank = rank;
        settlementUID = settlement.getUid();
    }
    
    /**
     * Gets the player's rank in the settlement associated with this data.
     * */
    public Rank getRank() {
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
        if (settlement == null) {
            settlement = server.getSettlement(settlementName);
        }
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
     * Sets the player's Rank in the settlement associated with this data.
     * 
     * @param rank New rank to associate.
     * */
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    /**
     * Sets the settlement associated with this data.
     * 
     * @param settlement New settlement to associate.
     * */
    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
        settlementName = settlement.getName();
        settlementUID = settlement.getUid();
    }
    
    public int getUid() {
        return settlementUID;
    }
    
    public void remove() {
        
    }
    
    public String toString() {
        return getClass().getName() + ": " + getSettlementName() + " (" + getUid() + ") " + rank + "; " + title;
    }
}
