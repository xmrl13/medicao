package strategies;

public class AdminRoleStrategy implements RoleStrategy {

    @Override
    public boolean canUpdateUser() {
        return true;
    }

    @Override
    public boolean canCreateMeasurement() {
        return true;
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canDeleteWork() {
        return true;
    }

    @Override
    public boolean canViewItems() {
        return true;
    }

    @Override
    public boolean canCreateUser() {
        return true;
    }

    @Override
    public boolean canDeleteUser() {
        return true;
    }

    @Override
    public boolean canReadUser() {
        return true;
    }

}
