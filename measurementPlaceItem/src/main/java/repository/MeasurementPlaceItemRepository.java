package repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import model.MeasurementPlaceItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MeasurementPlaceItemRepository extends R2dbcRepository<MeasurementPlaceItem, Long> {

    Mono<MeasurementPlaceItem> findByPlaceNameAndProjectContractAndItemNameAndItemUnitAndYearMonth(String placeName, String projectContract, String itemName, String itemUnit, String yearMonth);
}

