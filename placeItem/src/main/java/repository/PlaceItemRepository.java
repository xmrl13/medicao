package repository;


import model.PlaceItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface PlaceItemRepository extends R2dbcRepository<PlaceItem, Long> {

    Mono<PlaceItem> findByItemNameAndItemUnitAndPlaceNameAndProjectContract(String itemName, String itemUnit, String placeName, String placeProjectContract);

    Flux<PlaceItem> findByProjectContractIn(List<String> contracts);


}
