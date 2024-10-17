package measurement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import measurement.dto.MeasurementDTO;
import measurement.service.MeasurementService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("api/measurement")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final RestTemplate restTemplate;


    public MeasurementController(MeasurementService measurementService, RestTemplate restTemplate) {
        this.measurementService = measurementService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMeasurement(@Valid @RequestBody MeasurementDTO measurementDTO, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);


        try {
            ResponseEntity<Boolean> response = restTemplate.exchange("http://localhost:8080/api/projects/exists/" + measurementDTO.getProjectContract(), HttpMethod.GET, entity, Boolean.class);
            if (Boolean.TRUE.equals(response.getBody())) {
                MeasurementDTO savedMeasurement = measurementService.createMedicao(measurementDTO);
                return ResponseEntity.ok(savedMeasurement);
            }
            return ResponseEntity.badRequest().body("Obra não encontrada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/existsbyid/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = measurementService.measuramenteExistsById(id);
        return ResponseEntity.ok(exists);
    }

}
