package measurementplaceitem.repository;

import jakarta.validation.constraints.NotNull;
import measurementplaceitem.model.MeasurementPlaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementPlaceItemRepository extends JpaRepository<MeasurementPlaceItem, Long> {

    boolean existsByMeasurementIdAndPlaceItemId(@NotNull Long measurementId, @NotNull Long placeItemId);

}
