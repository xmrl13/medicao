package controller;

import dto.PlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.PlaceService;

@RestController
@RequestMapping("api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPlace(@RequestBody PlaceDTO placeDTO, @RequestHeader("Authorization") String token) {
        return placeService.createPlace(placeDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deletePlace(@RequestBody PlaceDTO placeDTO, @RequestHeader("Authorization") String token) {
        return placeService.deletePlace(placeDTO, token);
    }

    @PostMapping("/existbynameandprojectcontract")
    public Mono<ResponseEntity<String>> existByNameAndProject(@RequestBody PlaceDTO placeDTO, @RequestHeader("Authorization") String token) {
        return placeService.existsByNameAndProjectContract(placeDTO, token);
    }
}

