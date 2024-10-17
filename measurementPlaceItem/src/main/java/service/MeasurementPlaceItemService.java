package measurementplaceitem.service;

import jakarta.validation.Valid;
import measurementplaceitem.dto.MeasurementPlaceItemDTO;
import measurementplaceitem.exceptions.MeasuramentePlaceItemAlreadyExistsException;
import measurementplaceitem.model.MeasurementPlaceItem;
import measurementplaceitem.repository.MeasurementPlaceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementPlaceItemService {

    @Autowired
    private MeasurementPlaceItemRepository measurementPlaceItemRepository;

    public void create(@Valid MeasurementPlaceItemDTO measurementPlaceItemDTO) {

        if (!measurementPlaceItemRepository.existsByMeasurementIdAndPlaceItemId(measurementPlaceItemDTO.getMeasurementId(), measurementPlaceItemDTO.getPlaceItemId())) {
            MeasurementPlaceItem measurementPlaceItem = new MeasurementPlaceItem(measurementPlaceItemDTO.getMeasurementId(), measurementPlaceItemDTO.getPlaceItemId(), measurementPlaceItemDTO.getMeasuredValue());
            measurementPlaceItemRepository.save(measurementPlaceItem);
        }else{
            throw new MeasuramentePlaceItemAlreadyExistsException("Este item já foi criado para essa medição");
        }
    }
}
