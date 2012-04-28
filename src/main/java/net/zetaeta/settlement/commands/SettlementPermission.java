package net.zetaeta.settlement.commands;

import net.zetaeta.libraries.commands.local.LocalPermission;

public class SettlementPermission extends LocalPermission {
	private String subPerm;
	private SettlementPermission parent;
	
	public static final MasterSettlementPermission MASTER_PERMISSION = new MasterSettlementPermission();
	
	public SettlementPermission(String permission) {
	    super(permission, MASTER_PERMISSION);
		subPerm = permission;
		parent = MASTER_PERMISSION;
	}
	
	public SettlementPermission(String permission, SettlementPermission parent) {
	    super(permission, parent);
		this.parent = parent;
		this.subPerm = permission;
	}
	
	public String getPermission() {
		return new StringBuilder().append(parent.getPermission()).append(".").append(subPerm).toString();
	}
	
	public String getParentPermission() {
		return parent.getPermission();
	}
	
	public String getSubPermission() {
		return subPerm;
	}
	
	public boolean isMasterPermission() {
		return false;
	}
	
	public SettlementPermission getParent() {
		return parent;
	}
	
	public static class MasterSettlementPermission extends SettlementPermission {
		
		public MasterSettlementPermission() {
			super(null, "settlement");
		}
		
		@Override
		public boolean isMasterPermission() {
			return true;
		}
	}
}
