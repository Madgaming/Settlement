package net.zetaeta.settlement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public final class FlatFileIO implements SettlementConstants {
    static Map<World, Collection<Chunk>> reusableWorldPlots;
    
    public static final int FILE_FORMAT_VERSION = 0;
    
    public static void saveSettlementV0_0(Settlement set, DataOutputStream dos) throws IOException {
        dos.writeInt(set.getUid());
        dos.writeUTF(set.getName());
        dos.writeUTF(set.getSlogan());
        dos.writeInt(set.getBonusPlots());
        if (set.getSpawn() != null) {
            dos.writeChar('[');
            UUID sWorldUid = set.getSpawn().getWorld().getUID();
            dos.writeLong(sWorldUid.getMostSignificantBits());
            dos.writeLong(sWorldUid.getLeastSignificantBits());
            dos.writeInt(set.getSpawn().getBlockX());
            dos.writeInt(set.getSpawn().getBlockY());
            dos.writeInt(set.getSpawn().getBlockZ());
            dos.writeFloat(set.getSpawn().getYaw());
            dos.writeFloat(set.getSpawn().getPitch());
            dos.writeChar(']');
        }
        else {
            dos.writeChar('|');
        }
        dos.writeUTF(set.getOwnerName());
        if (set.getModeratorNames().size() > 0) {
            dos.writeChar('{');
            for (String s : set.getModeratorNames()) {
                dos.writeUTF(s);
                dos.writeChar(',');
            }
            dos.writeChar('}');
        }
        else {
            dos.writeChar('|');
        }
        if (set.getBaseMemberNames().size() > 0) {
            dos.writeChar('{');
            for (String s : set.getBaseMemberNames()) {
                dos.writeUTF(s);
                dos.writeChar(',');
            }
            dos.writeChar('}');
        }
        else {
            dos.writeChar('|');
        }
        if (ConfigurationConstants.useSettlementWorldCacheing && set.worldPlots != null) {
            if (set.worldPlots.size() > 0) {
                dos.writeChar('{');
                for (World wrld : set.worldPlots.keySet()) {
                    Collection<Chunk> chunks = set.worldPlots.get(wrld);
                    if (chunks.size() > 0) {
                        dos.writeChar('{');
                        dos.writeLong(wrld.getUID().getMostSignificantBits());
                        dos.writeLong(wrld.getUID().getLeastSignificantBits());
                        dos.writeChar(':');
                        for (Chunk ch : chunks) {
                            dos.writeChar(',');
                            dos.writeInt(ch.getX());
                            dos.writeInt(ch.getZ());
                        }
                        dos.writeChar('}');
                        dos.writeChar(';');
                    }
                    else {
                        dos.writeChar('|');
                    }
                }
                dos.writeChar('}');
            }
            else {
                dos.writeChar('|');
            }
        } else {
            if (set.getPlots().size() > 0) {
                if (reusableWorldPlots == null) {
                    reusableWorldPlots = new HashMap<World, Collection<Chunk>>();
                }
                for (Chunk ch : set.getPlots()) {
                    if (reusableWorldPlots.get(ch.getWorld()) == null) {
                        reusableWorldPlots.put(ch.getWorld(), new HashSet<Chunk>());
                    }
                    reusableWorldPlots.get(ch.getWorld()).add(ch);
                }
                dos.writeChar('{');
                for (World wrld : reusableWorldPlots.keySet()) {
                    Collection<Chunk> chunks = reusableWorldPlots.get(wrld);
                    if (chunks.size() > 0) {
                        dos.writeChar('{');
                        dos.writeLong(wrld.getUID().getMostSignificantBits());
                        dos.writeLong(wrld.getUID().getLeastSignificantBits());
                        dos.writeChar(':');
                        for (Chunk ch : chunks) {
                            dos.writeChar(',');
                            dos.writeInt(ch.getX());
                            dos.writeInt(ch.getZ());
                        }
                        dos.writeChar('}');
                        dos.writeChar(';');
                    }
                    else {
                        dos.writeChar('|');
                    }
                }
                dos.writeChar('}');
            }
            else {
                dos.writeChar('|');
            }
        }
            
        dos.writeChar('\n');
    }
    
    public static Settlement loadSettlementV0_0(DataInputStream dis) throws IOException {
        int uid = dis.readInt();
        String name = dis.readUTF();
        Settlement set = new Settlement(name, uid);
        set.setSlogan(dis.readUTF());
        set.setBonusPlots(dis.readInt());
        
        // Spawn location
        if (dis.readChar() == '[') { // [
            long sUidStart = dis.readLong();
            long sUidEnd = dis.readLong();
            int x = dis.readInt();
            int y = dis.readInt();
            int z = dis.readInt();
            float yaw = dis.readFloat();
            float pitch = dis.readFloat();
            dis.readChar(); // ]
            UUID sWorldUid = new UUID(sUidStart, sUidEnd);
            World world = Bukkit.getWorld(sWorldUid);
            if (world != null) {
                set.setSpawn(new Location(world, x, y, z, yaw, pitch));
            }
        }
        // done spawn
        
        String ownerName = dis.readUTF();
        set.setOwnerName(ownerName);
        
//        dis.readChar(); // {
        char c = dis.readChar();
        while (c != '|' && c != '}') {
            set.addModerator(dis.readUTF());
            c = dis.readChar();
        } // }
//        dis.readChar(); // {
        c = dis.readChar();
        while (c != '|' && c != '}') {
            set.addMember(dis.readUTF());
            c = dis.readChar();
        } // }
        c = dis.readChar();
        while (c != '|' && c != '}') { // Worlds
            c = dis.readChar(); // { / |
            if (c == '|') {
                continue;
            }
            long uidStart = dis.readLong();
            long uidEnd = dis.readLong();
            UUID worldUid = new UUID(uidStart, uidEnd);
            World currWorld = Bukkit.getWorld(worldUid);
            dis.readChar();
            c = dis.readChar();
            while (c != '}') {
                int cx = dis.readInt();
                int cz = dis.readInt();
                Chunk chk = currWorld.getChunkAt(cx, cz);
                set.addChunk(chk);
                c = dis.readChar();
            }
            dis.readChar();
            c = dis.readChar();
        }
        dis.readChar(); // \n
        set.updateMembers();
        set.updateClaimablePlots();
        return set;
    }
    
    public static void savePlayerV0_0(SettlementPlayer sPlayer, DataOutputStream dos) throws IOException {
        dos.writeInt(FILE_FORMAT_VERSION); // Save format version
        dos.writeLong(sPlayer.lastOnline);
        dos.writeChar('{');
        for (SettlementData data : sPlayer.getData()) {
            dos.writeChar(';');
            saveSettlementDataV0_0(data, dos);
        }
        dos.writeChar('}');
    }
    
    public static void saveSettlementDataV0_0(SettlementData data, DataOutputStream dos) throws IOException {
        dos.writeChar('[');
        dos.writeInt(data.getUid());
        dos.writeInt(data.getRank().getPriority());
        dos.writeUTF(data.getTitle());
        dos.writeChar(']');
    }
    
    public static void loadPlayerV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        sPlayer.lastOnline = dis.readLong();
        char c = dis.readChar();
        if (c == '{') {
            while (dis.readChar() != '}') {
                SettlementData sd = loadDataV0_0(sPlayer, dis);
                if (sd != null) {
                    sPlayer.addData(sd);
                }
            }
        }
    }
    
    public static SettlementData loadDataV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        if (dis.readChar() != '[') {
            return null;
        }
        int uid = dis.readInt();
        int pri = dis.readInt();
        String title = dis.readUTF();
        if (dis.readChar() != ']') {
            return null;
        }
        Settlement set = Settlement.getSettlement(uid);
        if (set == null) {
            return null;
        }
        switch (pri) {
        case 0 :
            return null;
        case 1 :
            return new SettlementData(set, SettlementRank.MEMBER, title);
        case 2 :
            return new SettlementData(set, SettlementRank.MOD, title);
        case 3 :
            return new SettlementData(set, SettlementRank.OWNER, title);
        default :
            log.warning("Player " + sPlayer.getName() + " had an invalid rank in " + set.getName());
            return null;
        }
    }
}
