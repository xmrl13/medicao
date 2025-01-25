package controller;

import dto.PlaceItemRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.PlaceItemService;

@RestController
@RequestMapping("api/placesitens")
public class PlaceItemController {

    private final PlaceItemService placeItemService;

    public PlaceItemController(PlaceItemService placeItemService) {
        this.placeItemService = placeItemService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPlaceItem(@RequestBody PlaceItemRequestDTO placeItemRequestDTO, @RequestHeader("Authorization") String token) {
        return placeItemService.createPlaceItem(placeItemRequestDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deletePlaceItem(@RequestBody PlaceItemRequestDTO placeItemRequestDTO, @RequestHeader("Authorization") String token) {
        return placeItemService.deletePlaceItem(placeItemRequestDTO, token);
    }

    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existPlaceItem(@RequestBody PlaceItemRequestDTO placeItemRequestDTO, @RequestHeader("Authorization") String token) {
        return placeItemService.existsByNameAndContract(placeItemRequestDTO, token);
    }

    @GetMapping("/get-places-itens-by-token")
    public Mono<ResponseEntity<?>> getAllPlacesItensByToken(@RequestHeader("Authorization") String token) {
        return placeItemService.getAllPlaceItensByToken(token);
    }
}