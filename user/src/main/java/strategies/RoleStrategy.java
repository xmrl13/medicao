package strategies;

public interface RoleStrategy {

    boolean canUpdateUser();
    boolean canCreateMeasurement();
    boolean canManageUsers();
    boolean canDeleteWork();
    boolean canViewItems();
    boolean canCreateUser();
    boolean canDeleteUser();
    boolean canReadUser();

}
