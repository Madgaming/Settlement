package net.zetaeta.settlement.object;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SettlementWorld {
    private World world;
    private Map<Chunk, Plot> plots = new ConcurrentHashMap<Chunk, Plot>();
    
    public SettlementWorld(World world) {
        this.world = world;
    }
    
    public Plot getPlot(Chunk chunk) {
        if (plots.containsKey(chunk)) {
            return plots.get(chunk);
        }
        return new Plot(this, chunk);
    }
    
    public Plot getPlot(int x, int z) {
        Chunk chunk = world.getChunkAt(x, z);
        if (plots.containsKey(chunk)) {
            return plots.get(chunk);
        }
        return new Plot(this, chunk);
    }
    
    public Plot getPlot(Location loc) {
        Chunk chunk = world.getChunkAt(loc);
        if (plots.containsKey(chunk)) {
            return plots.get(chunk);
        }
        return new Plot(this, chunk);
    }
    
    public Plot getPlot(Block block) {
        Chunk chunk = world.getChunkAt(block);
        if (plots.containsKey(chunk)) {
            return plots.get(chunk);
        }
        return new Plot(this, chunk);
    }
}
