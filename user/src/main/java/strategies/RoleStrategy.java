package strategies;

public interface RoleStrategy {

    boolean createUser();

    boolean readUser();

    boolean updateUser();

    boolean deleteUser();

    boolean createItem();

    boolean deleteItem();

    boolean existItem();

    boolean createProject();

    boolean deleteProject();

    boolean existProject();

    boolean getProject();

    boolean addEmailInProject();

    boolean createPlace();

    boolean deletePlace();

    boolean existPlace();

    boolean createMeasurement();

    boolean deleteMeasurement();

    boolean existMeasurement();

    boolean createPlaceItem();

    boolean deletePlaceItem();

    boolean existPlaceItem();

    boolean getPlaceItem();

    boolean createMeasurementPlaceItem();

    boolean deleteMeasurementPlaceItem();

    boolean existMeasurementPlaceItem();
}
