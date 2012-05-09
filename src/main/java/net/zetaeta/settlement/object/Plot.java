package net.zetaeta.settlement.object;

import org.bukkit.Chunk;

public class Plot {
    private Chunk chunk;
    private SettlementWorld world;
    private Settlement ownerSettlement;
    private SettlementPlayer ownerPlayer;
    
    public Plot(SettlementWorld world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;
        ownerSettlement = Settlement.WILDERNESS;
        ownerPlayer = SettlementPlayer.NONE;
    }
    
    public Chunk getChunk() {
        return chunk;
    }
    
    public Settlement getOwnerSettlement() {
        return ownerSettlement;
    }
    
    public SettlementPlayer getOwnerPlayer() {
        return ownerPlayer;
    }
    
    
}
