package measurementplaceitem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import measurementplaceitem.dto.MeasurementPlaceItemDTO;
import measurementplaceitem.service.MeasurementPlaceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("api/measurementplaceitem")
public class MeasurementPlaceItemController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MeasurementPlaceItemService measurementPlaceItemService;


    //TODO verificar se o usuario é cordenador ou engenheiro filiado a obra para criar um measurementplaceitem
    @PostMapping("/create")
    public ResponseEntity<?> createPlaceItem(@Valid @RequestBody MeasurementPlaceItemDTO measurementPlaceItemDTO, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Boolean> responsePlace = restTemplate.exchange("http://localhost:8080/api/placeitem/existsbyid/" + measurementPlaceItemDTO.getPlaceItemId(), HttpMethod.GET, entity, Boolean.class);
            ResponseEntity<Boolean> responseItem = restTemplate.exchange("http://localhost:8080/api/measurement/existsbyid/" + measurementPlaceItemDTO.getMeasurementId(), HttpMethod.GET, entity, Boolean.class);

            if (Boolean.TRUE.equals(responsePlace.getBody()) && Boolean.TRUE.equals(responseItem.getBody())) {
                measurementPlaceItemService.create(measurementPlaceItemDTO);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Bacia ou item não encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
