package net.zetaeta.plugins.settlement;


public enum SettlementRank {
	MEMBER(0, SettlementPlugin.plugin.config.getString("settlement.appearance.names.member")), 
	MOD(1, SettlementPlugin.plugin.config.getString("settlement.appearance.names.mod")), 
	OWNER(2, SettlementPlugin.plugin.config.getString("settlement.appearance.names.owner"));
	
	int priority;
	String name;
	
	private SettlementRank(int pri, String name) {
		priority = pri;
		this.name = name;
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
