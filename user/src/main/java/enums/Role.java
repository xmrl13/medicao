package enums;


import strategies.*;

public enum Role {
    ADMIN(new AdminRoleStrategy()),
    TECHNICIAN(new TechnicianRoleStrategy()),
    ENGINEER(new EngineerRoleStrategy()),
    COORDINATOR(new CoordinatorRoleStrategy());

    private final RoleStrategy strategy;

    Role(RoleStrategy strategy) {
        this.strategy = strategy;
    }

    public RoleStrategy getStrategy() {
        return strategy;
    }

    public boolean canUpdateUser() {
        return strategy.canUpdateUser();
    }

    public boolean canCreateMeasurement() {
        return strategy.canCreateMeasurement();
    }

    public boolean canManageUsers() {
        return strategy.canManageUsers();
    }

    public boolean canDeleteWork() {
        return strategy.canDeleteWork();
    }

    public boolean canViewItems() {
        return strategy.canViewItems();
    }

    public boolean canCreateUser() {
        return strategy.canCreateUser();
    }

    public boolean canDeleteUser() {
        return strategy.canDeleteUser();
    }

    public boolean canReadUser() {
        return strategy.canReadUser();
    }

}
