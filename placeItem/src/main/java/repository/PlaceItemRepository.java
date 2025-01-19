package repository;


import model.PlaceItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlaceItemRepository extends R2dbcRepository<PlaceItem, Long> {

    Mono<PlaceItem> findByItemNameAndItemUnitAndPlaceNameAndPlaceProjectContract(String itemName, String itemUnit, String placeName, String placeProjectContract);
}
