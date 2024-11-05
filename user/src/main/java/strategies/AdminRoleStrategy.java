package strategies;

public class AdminRoleStrategy implements RoleStrategy {

    @Override
    public boolean createUser() {
        return true;
    }

    @Override
    public boolean readUser() {
        return true;
    }

    @Override
    public boolean updateUser() {
        return true;
    }

    @Override
    public boolean deleteUser() {
        return true;
    }

    @Override
    public boolean createItem() {
        return true;
    }

    @Override
    public boolean deleteItem() {
        return true;
    }

    @Override
    public boolean existItem() {
        return true;
    }

    @Override
    public boolean createProject() {
        return true;
    }

    @Override
    public boolean deleteProject() {
        return true;
    }

    @Override
    public boolean existProject() {
        return true;
    }

    @Override
    public boolean createPlace() {
        return true;
    }

    @Override
    public boolean deletePlace() {
        return true;
    }

    @Override
    public boolean existPlace() {
        return true;
    }
}
