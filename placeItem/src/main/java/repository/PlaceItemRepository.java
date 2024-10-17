package placeitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import placeitem.model.PlaceItem;

import java.util.Optional;

@Repository
public interface PlaceItemRepository extends JpaRepository<PlaceItem, Long> {

    Optional<PlaceItem> findByPlaceIdAndItemId(Long placeId, Long itemId);

    boolean findByPlaceId(Long id);

}
