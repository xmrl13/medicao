package placeitem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import placeitem.dto.PlaceItemDTO;
import placeitem.model.PlaceItem;
import placeitem.repository.PlaceItemRepository;

@Service
public class PlaceItemService {

    @Autowired
    private PlaceItemRepository placeItemRepository;

    @Transactional
    public ResponseEntity<?> create(PlaceItemDTO placeItemDTO) {

        placeItemRepository.findByPlaceIdAndItemId(placeItemDTO.getPlaceId(), placeItemDTO.getItemId()).ifPresent(placeItem -> {
            throw new IllegalArgumentException("BaciaItem já existente");
        });


        PlaceItem placeItem = new PlaceItem(placeItemDTO.getPlaceId(), placeItemDTO.getItemId(), placeItemDTO.getPredictedValue());
        placeItemRepository.save(placeItem);
        return ResponseEntity.ok().build();
    }

    public boolean placeItemExistsById(Long id) {
        return placeItemRepository.existsById(id);
    }
}
