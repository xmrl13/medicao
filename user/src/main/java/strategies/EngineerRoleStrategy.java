package strategies;

public class EngineerRoleStrategy implements RoleStrategy {

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
        return false;
    }

    @Override
    public boolean canDeleteWork() {
        return false;
    }

    @Override
    public boolean canViewItems() {
        return true;
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
