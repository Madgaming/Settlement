package net.zetaeta.settlement;


public enum Rank {
    OUTSIDER(0, ConfigurationConstants.outsiderName),
    MEMBER(1, ConfigurationConstants.memberName), 
    MODERATOR(2, ConfigurationConstants.modName), 
    OWNER(3, ConfigurationConstants.ownerName);
    
    private int priority;
    private String name;
    
    private Rank(int pri, String name) {
        priority = pri;
        this.name = name;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getName() {
        return name;
    }
    
    public static Rank getSuperior(Rank a, Rank b) {
        return a.priority > b.priority ? a : b;
    }
    
    public boolean isSuperiorTo(Rank other) {
        return this.priority > other.priority;
    }
    
    public static Rank getByPriority(int priority) {
        switch  (priority) {
        case 0 :
            return OUTSIDER;
        case 1 :
            return MEMBER;
        case 2 :
            return MODERATOR;
        case 3 :
            return OWNER;
        default :
            return null;
        }
    }
    
    public boolean isEqualOrSuperiorTo(Rank other) {
        return this.priority >= other.priority;
    }
}
