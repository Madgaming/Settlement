package net.zetaeta.settlement.object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.zetaeta.settlement.FlatFileIO;
import net.zetaeta.settlement.SettlementConstants;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SettlementWorld implements SettlementConstants{
    private World world;
    private Map<ChunkCoordinate, Plot> plots = new ConcurrentHashMap<ChunkCoordinate, Plot>();
    
    public SettlementWorld(World world) {
        this.world = world;
        for (Chunk chunk : world.getLoadedChunks()) {
            loadPlot(new ChunkCoordinate(chunk));
        }
    }
    
    public File getPlotsFolder() {
        File pFolder = new File(plugin.getPlotsFolder(), world.getName());
        pFolder.mkdirs();
        return pFolder;
    }
    
    public Plot getPlot(ChunkCoordinate location) {
        if (plots.containsKey(location)) {
            return plots.get(location);
        }
//        Plot p = new Plot(this, location);
//        plots.put(location, p);
        return null;
    }
    
    public Plot getPlot(Chunk chunk) {
        ChunkCoordinate cc = new ChunkCoordinate(chunk);
        return getPlot(cc);
    }
    
    public Plot getPlot(int x, int z) {
        ChunkCoordinate cc = new ChunkCoordinate(x, 0, z);
        return getPlot(cc);
    }
    
    public Plot getPlot(Location loc) {
        ChunkCoordinate cc = new ChunkCoordinate(loc);
        return getPlot(cc);
    }
    
    public Plot getPlot(Block block) {
        ChunkCoordinate cc = new ChunkCoordinate(block.getLocation());
        return getPlot(cc);
    }
    
    public void loadPlot(ChunkCoordinate coords) {
        if (getPlot(coords) != null) {
            return;
        }
        int superChunkX = coords.x >> 2, superChunkZ = coords.z >> 2;
        File file = new File(plugin.getPlotsFolder(), "plots@" + superChunkX + "," + superChunkZ);
        if (!file.exists()) {
            plots.put(coords, new Plot(this, coords));
            return;
        }
        loadPlots(file);
    }
    
    public void loadPlots(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("file must exist!");
        }
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int version = dis.readInt();
            if (version == 0) {
                while (dis.available() > 0) {
                    Plot plot = null;
                    try {
                        plot = FlatFileIO.loadPlotV0_0(dis);
                    }
                    catch (Throwable thrown) {
                        log.severe("Error occurred while loading plot " + (plot == null ? "" : plot.toString()) + ": " + thrown.getClass().getName());
                        log.log(Level.SEVERE, "Error loading plots!", thrown);
                        while (dis.readChar() != '\n') {
                            
                        }
                    }
                    dis.readChar();
                }
            }
            else {
                log.severe("Error reading from plots file " + file.getName() + " Unsupported format version: " + version);
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not load plots file " + file.getName(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading plots file " + file.getName(), e);
        }
    }
    
    public void unloadPlot(ChunkCoordinate coords) {
        Collection<ChunkCoordinate> group = coords.getCoordsGroup();
        for (ChunkCoordinate other : group) {
            if (plots.get(other) == null) {
                continue;
            }
            if (plots.get(other).getChunk().isLoaded()) {
                return;
            }
        }
        int superChunkX = coords.x >> 2, superChunkZ = coords.z >> 2;
        File file = new File(plugin.getPlotsFolder(), "plots@" + superChunkX + "," + superChunkZ);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create plot file for " + file.getName(), e);
                return;
            }
        }
        Collection<Plot> plots = new ArrayList<Plot>(group.size());
        for (ChunkCoordinate coord : group) {
            plots.add(getPlot(coord));
        }
        unloadPlots(plots, file);
    }
    
    public void unloadPlots(Collection<Plot> plots, File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("file must exist!");
        }
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            dos.writeInt(FlatFileIO.PLOT_FILE_VERSION);
            for (Plot plot : plots) {
                try {
                    FlatFileIO.savePlotV0_0(plot, dos);
                }
                catch (Throwable thrown) {
                    log.severe("Error occurred while saving plot " + (plot == null ? "" : plot.toString()) + ": " + thrown.getClass().getName());
                    log.log(Level.SEVERE, "Error saving plots!", thrown);
                }
                dos.writeChar('\n');
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not load plots file " + file.getName(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading plots file " + file.getName(), e);
        }
    }
    
    public Chunk getChunk(ChunkCoordinate location) {
        return world.getChunkAt(location.x << 4, location.z << 4);
    }

    public World getWorld() {
        return world;
    }
}
