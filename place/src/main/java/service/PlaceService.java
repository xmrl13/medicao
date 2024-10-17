package place.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import place.dto.PlaceDTO;
import place.model.Place;
import place.repository.PlaceRepository;

import java.util.Optional;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Transactional
    public void createPlace(PlaceDTO placeDTO) {

        placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract()).ifPresent(place -> {
            throw new IllegalArgumentException(String.format("A bacia: %s já existe para o contrato: %s", placeDTO.getName(), placeDTO.getProjectContract()));
        });

        Place placeSaved = new Place(placeDTO.getName(), placeDTO.getProjectContract());
        placeRepository.save(placeSaved);
        ResponseEntity.ok().build();
    }

    public Optional<Long> getIdByNameAndProjectContract(String name, String projectContract) {
        return placeRepository.findPlaceIdByNameAndProjectContract(name, projectContract);
    }
}
