package controller;

import dto.MeasurementPlaceItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.MeasurementPlaceItemService;


@RestController
@RequestMapping("api/measurementsplacesitens")
public class MeasurementPlaceItemController {

    @Autowired
    private MeasurementPlaceItemService measurementPlaceItemService;

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createMeasurement(@RequestBody MeasurementPlaceItemDTO measurementPlaceItemDTO, @RequestHeader("Authorization") String token) {
        System.out.println("Executei");
        return measurementPlaceItemService.createMeasurementPlaceItem(measurementPlaceItemDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteMeasurement(@RequestBody MeasurementPlaceItemDTO measurementPlaceItemDTO, @RequestHeader("Authorization") String token) {
        return measurementPlaceItemService.deleteMeasurement(measurementPlaceItemDTO, token);
    }

    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existMeasurementPlaceItem(@RequestBody MeasurementPlaceItemDTO measurementPlaceItemDTO, @RequestHeader("Authorization") String token) {
        return measurementPlaceItemService.existsByPlaceNameProjectContractItemNameItemUnitYearMonth(measurementPlaceItemDTO, token);
    }
}
