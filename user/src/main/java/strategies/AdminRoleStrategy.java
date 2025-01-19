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

    @Override
    public boolean createMeasurement() {
        return true;
    }

    @Override
    public boolean deleteMeasurement() {
        return true;
    }

    @Override
    public boolean existMeasurement() {
        return true;
    }

    @Override
    public boolean createPlaceItem() {
        return true;
    }

    @Override
    public boolean deletePlaceItem() {
        return true;
    }

    @Override
    public boolean existPlaceItem() {
        return true;
    }

    @Override
    public boolean createMeasurementPlaceItem() {
        return true;
    }

    @Override
    public boolean deleteMeasurementPlaceItem() {
        return true;
    }

    @Override
    public boolean existMeasurementPlaceItem() {
        return true;
    }
}
