package net.zetaeta.settlement.object;

import net.zetaeta.settlement.SettlementConstants;

import org.bukkit.Chunk;

public class Plot implements SettlementConstants {
    private ChunkCoordinate location;
    private SettlementWorld world;
    private Settlement ownerSettlement;
//    private SettlementPlayer ownerPlayer;
    private String ownerPlayerName;
    
    public Plot(SettlementWorld world, ChunkCoordinate cc) {
        this.world = world;
        this.location = cc;
        ownerSettlement = Settlement.WILDERNESS;
        ownerPlayerName = "";
    }
    
    public Chunk getChunk() {
        return world.getChunk(location);
    }
    
    public Settlement getOwnerSettlement() {
        return ownerSettlement;
    }
    
    public void setOwnerSettlement(Settlement owner) {
        ownerSettlement = owner;
    }
    
    public SettlementPlayer getOwnerPlayer() {
        return server.getSettlementPlayer(ownerPlayerName);
    }
    
    public void setOwnerPlayer(SettlementPlayer owner) {
        ownerPlayerName = owner.getName();
    }
    
    public void setOwnerPlayer(String ownerName) {
        ownerPlayerName = ownerName;
    }
    
    public ChunkCoordinate getCoordinates() {
        return location;
    }

    public SettlementWorld getWorld() {
        return world;
    }

}
