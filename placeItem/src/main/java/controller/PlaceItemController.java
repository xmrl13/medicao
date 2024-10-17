package placeitem.controller;

import item.src.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import placeitem.dto.PlaceItemDTO;
import placeitem.dto.PlaceItemRequestDTO;
import placeitem.service.PlaceItemService;

@RestController
@RequestMapping("api/placeitem")
public class PlaceItemController {


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PlaceItemService placeItemService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createPlaceItem(@Valid @RequestBody PlaceItemRequestDTO placeItemRequestDTO, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String itemUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/itens/getidbynameandunit/")
                    .pathSegment(placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit())
                    .toUriString();


            String placeUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/places/getidbynameandprojectcontract/")
                    .pathSegment(placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getPlaceProjectContract())
                    .toUriString();

            ResponseEntity<Long> responsePlace = restTemplate.exchange(placeUrl, HttpMethod.GET, entity, Long.class);
            ResponseEntity<Long> responseItem = restTemplate.exchange(itemUrl, HttpMethod.GET, entity, Long.class);

            Long placeId = responsePlace.getBody();
            Long itemId = responseItem.getBody();

            if (placeId == null) {
                throw new ResourceNotFoundException("Bacia não encontrada");
            }

            if (itemId == null) {
                throw new ResourceNotFoundException("Item não encontrado");
            }

            PlaceItemDTO placeItemDTO = new PlaceItemDTO(placeId, itemId, placeItemRequestDTO.getPredictedValue());
            placeItemService.create(placeItemDTO);
            return ResponseEntity.ok().build();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/existsbyid/{id}")
    public ResponseEntity<Boolean> existsBy(@PathVariable Long id) {
        boolean exists = placeItemService.placeItemExistsById(id);
        return ResponseEntity.ok(exists);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.ok().body(e.getMessage());
    }
}
