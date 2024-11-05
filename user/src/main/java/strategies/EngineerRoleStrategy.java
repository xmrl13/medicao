package strategies;

public class EngineerRoleStrategy implements RoleStrategy {

    @Override
    public boolean createUser() {
        return false;
    }

    @Override
    public boolean readUser() {
        return false;
    }

    @Override
    public boolean updateUser() {
        return true;
    }

    @Override
    public boolean deleteUser() {
        return false;
    }

    @Override
    public boolean createItem() {
        return true;
    }

    @Override
    public boolean deleteItem() {
        return false;
    }

    @Override
    public boolean existItem() {
        return true;
    }

    @Override
    public boolean createProject() {
        return false;
    }

    @Override
    public boolean deleteProject() {
        return false;
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
