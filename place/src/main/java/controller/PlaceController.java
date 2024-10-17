package place.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import place.dto.PlaceDTO;
import place.service.PlaceService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/places")
public class PlaceController {

    private final RestTemplate restTemplate;

    private final PlaceService placeService;

    public PlaceController(RestTemplate restTemplate, PlaceService placeService) {
        this.restTemplate = restTemplate;
        this.placeService = placeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlaces(@Valid @RequestBody PlaceDTO placeDTO, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange("http://localhost:8080/api/projects/exists/" + placeDTO.getProjectContract(), HttpMethod.GET, entity, Boolean.class);

            if (Boolean.TRUE.equals(response.getBody())) {
                placeService.createPlace(placeDTO);
                return ResponseEntity.ok().build();

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra não encontrada para o contrato: " + placeDTO.getProjectContract());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/getidbynameandprojectcontract/{name}/{projectContract}")
    public ResponseEntity<?>getIdByNameAndProjectContract(@PathVariable  String name, @PathVariable String projectContract) {

        String decodeName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String decodeProjectContrac = URLDecoder.decode(projectContract, StandardCharsets.UTF_8);

        System.out.println(decodeName);
        System.out.println(decodeProjectContrac);

        Optional<Long> id = placeService.getIdByNameAndProjectContract(decodeName, decodeProjectContrac);
        return ResponseEntity.ok(id);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de integridade de dados: " + Objects.requireNonNull(ex.getRootCause()).getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + ex.getMessage());
    }
}