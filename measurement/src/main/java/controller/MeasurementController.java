package controller;

import dto.MeasurementDTO;
import dto.MeasurementRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.MeasurementService;


@RestController
@RequestMapping("api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createMeasurement(@RequestBody MeasurementDTO measurementDTO, @RequestHeader("Authorization") String token) {
        return measurementService.createMeasurement(measurementDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteMeasurement(@RequestBody MeasurementDTO measurementDTO, @RequestHeader("Authorization") String token) {
        return measurementService.deleteMeasurement(measurementDTO, token);
    }

    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existMeasurement(@RequestBody MeasurementRequestDTO measurementRequestDTO, @RequestHeader("Authorization") String token) {
        return measurementService.existsByNameAndContract(measurementRequestDTO, token);
    }

}
