package net.zetaeta.settlement.commands;

import java.util.HashMap;
import java.util.Map;

import net.zetaeta.libraries.commands.local.SolidLocalPermission;

/**
 * 
 * @author Zetaeta
 * @deprecated This was a bad idea in the first place.
 */
@Deprecated
public class SettlementPermission extends SolidLocalPermission {
    public static final SettlementPermission MASTER_PERMISSION;
    public static final SettlementPermission USE_PERMISSION;
    public static final SettlementPermission USE_BASIC_PERMISSION;
    public static final SettlementPermission USE_OWNER_PERMISSION;
    public static final SettlementPermission ADMIN_PERMISSION;
    public static final SettlementPermission ADMIN_BASIC_PERMISSION;
    public static final SettlementPermission ADMIN_OWNER_PERMISSION;
    private SettlementPermission parent;
    private SettlementPermission adminPermission;
    private Map<String, SettlementPermission> children = new HashMap<String, SettlementPermission>();
    
    static {
        MASTER_PERMISSION = new SettlementPermission("settlement", null) {
            @Override
            public boolean isMasterPermission() {
                return true;
            }
            @Override
            public SettlementPermission getAdminPermission() {
                return ADMIN_PERMISSION;
            }
        };
        USE_PERMISSION = new SettlementPermission("use", MASTER_PERMISSION);
        USE_BASIC_PERMISSION = new SettlementPermission("basic", USE_PERMISSION) {
            @Override
            public SettlementPermission getAdminPermission() {
                return ADMIN_BASIC_PERMISSION;
            }
        };
        USE_OWNER_PERMISSION = new SettlementPermission("owner", USE_PERMISSION) {
            @Override
            public SettlementPermission getAdminPermission() {
                return ADMIN_OWNER_PERMISSION;
            }
        };
        ADMIN_PERMISSION = new SettlementPermission("admin", MASTER_PERMISSION) {
            @Override
            public SettlementPermission getAdminPermission() {
                return this;
            }
        };
        ADMIN_BASIC_PERMISSION = new SettlementPermission("basic", ADMIN_PERMISSION) {
            @Override
            public SettlementPermission getAdminPermission() {
                return this;
            }
        };
        ADMIN_OWNER_PERMISSION = new SettlementPermission("owner", ADMIN_PERMISSION) {
            @Override
            public SettlementPermission getAdminPermission() {
                return this;
            }
        };
    }
    
    public SettlementPermission(String permission) {
        super(permission, MASTER_PERMISSION);
        parent = MASTER_PERMISSION;
    }
    
    public SettlementPermission(String permission, SettlementPermission parent) {
        super(permission, parent);
        this.parent = parent;
    }
    
    public SettlementPermission getAdminPermission() {
        if (adminPermission != null) {
            return adminPermission;
        }
        if (isMasterPermission()) {
            return (adminPermission = new SettlementPermission("admin", this));
        }
        return (adminPermission = new SettlementPermission(subPermission, parent.getAdminPermission()));
    }
    
    public boolean registerChild(SettlementPermission child) {
        if (children.containsKey(child.getSubPermission())) {
            return false;
        }
        children.put(child.getSubPermission(), child);
        return true;
    }
    
    public SettlementPermission getChild(String childName) {
        return children.get(childName);
    }
}
