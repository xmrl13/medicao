package strategies;

public class TechnicianRoleStrategy implements RoleStrategy {

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

    @Override
    public boolean createMeasurement() {
        return false;
    }

    @Override
    public boolean deleteMeasurement() {
        return false;
    }

    @Override
    public boolean existMeasurement() {
        return true;
    }

    @Override
    public boolean createPlaceItem() {
        return false;
    }

    @Override
    public boolean deletePlaceItem() {
        return false;
    }

    @Override
    public boolean existPlaceItem() {
        return true;
    }

    @Override
    public boolean createMeasurementPlaceItem() {
        return false;
    }

    @Override
    public boolean deleteMeasurementPlaceItem() {
        return false;
    }

    @Override
    public boolean existMeasurementPlaceItem() {
        return true;
    }
}

