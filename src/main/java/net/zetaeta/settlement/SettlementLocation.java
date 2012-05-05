package net.zetaeta.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.bukkit.World;
import org.bukkit.util.Vector;

public class SettlementLocation extends Vector implements Externalizable {

    protected World world;
    protected float pitch, yaw;
    
    public SettlementLocation(World world, double x, double y, double z) {
        super(x, y, z);
        this.world = world;
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
    
    public World getWorld() {
        return world;
    }
}
