package net.zetaeta.settlement;

public class ConfigurationConstants {
    
    /**
     * Whether to cache which Settlements are in which world.
     */
    public static boolean useSettlementWorldCacheing = false;
    public static boolean useChunkOwnershipCacheing = true;
    public static boolean useMultithreading = true;
    public static boolean multithreadedShutdown = false;

    public static int plotsPerPlayer = 16;
    public static int chunkOwnershipCacheSize = 50;

    public static String outsiderName = "Outsider";
    public static String memberName = "Member";
    public static String modName = "Moderator";
    public static String ownerName = "Owner";
    public static String wildernessMessage = "§b~ §6Wilderness";
    public static String denyBuildMessage = "§cYou are not allowed to build in the Settlement §6%s";
    public static String denyBreakMessage = "§cYou are not allowed to destroy in the Settlement §6%s";
}
