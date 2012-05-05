package net.zetaeta.settlement;


public enum SettlementRank {
    MEMBER(0, ConfigurationConstants.memberName), 
    MOD(1, ConfigurationConstants.modName), 
    OWNER(2, ConfigurationConstants.ownerName);
    
    private int priority;
    private String name;
    
    private SettlementRank(int pri, String name) {
        priority = pri;
        this.name = name;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getName() {
        return name;
    }
    
    public static SettlementRank getSuperior(SettlementRank a, SettlementRank b) {
        return a.priority > b.priority ? a : b;
    }
    
    public boolean isSuperiorTo(SettlementRank other) {
        return this.priority > other.priority;
    }
    
    public static SettlementRank getByPriority(int priority) {
        switch  (priority) {
        case 0 :
            return MEMBER;
        case 1 :
            return MOD;
        case 2 :
            return OWNER;
        default :
            return null;
        }
    }
    
    public boolean isEqualOrSuperiorTo(SettlementRank other) {
        return this.priority >= other.priority;
    }
}