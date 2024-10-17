package strategies;

public class TechnicianRoleStrategy implements RoleStrategy {

    @Override
    public boolean canUpdateUser() {
        return true;
    }

    @Override
    public boolean canCreateMeasurement() {
        return false; // Technician cannot create measurements
    }

    @Override
    public boolean canManageUsers() {
        return false; // Technician cannot manage users
    }

    @Override
    public boolean canDeleteWork() {
        return false; // Technician cannot delete works
    }

    @Override
    public boolean canViewItems() {
        return true; // Technician can view items, but with some limitations
    }

    @Override
    public boolean canCreateUser() {
        return false;
    }

    @Override
    public boolean canDeleteUser() {
        return false;
    }

    @Override
    public boolean canReadUser() {
        return false;
    }

}

