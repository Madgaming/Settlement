package net.zetaeta.settlement.object;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkCoordinate {
    public final int x, z;
    
    public ChunkCoordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkCoordinate(Chunk chunk) {
        x = chunk.getX();
        z = chunk.getZ();
    }
    
    public ChunkCoordinate(int x, int y, int z) {
        this.x = x >> 4;
        this.z = z >> 4;
    }
    
    public ChunkCoordinate(Location location) {
        x = location.getBlockX() >> 4;
        z = location.getBlockZ() >> 4;
    }
    
    public Collection<ChunkCoordinate> getCoordsGroup() {
        ChunkCoordinate groupBase = new ChunkCoordinate(x >> 2, z >> 2);
        Collection<ChunkCoordinate> coords = new ArrayList<ChunkCoordinate>(16);
        for (int x=0; x<4; ++x) {
            for (int z=0; z<4; ++z) {
                coords.add(new ChunkCoordinate(x, z));
            }
        }
        return coords;
    }
    
    public boolean equals(Object other) {
        if (!(other instanceof ChunkCoordinate)) {
            return false;
        }
        return this == other || ((x == ((ChunkCoordinate) other).x) && (z == ((ChunkCoordinate) other).z)); 
    }
    
    public int hashCode() {
        return (x << 16) | (z & 0xfffff);
    }
}
