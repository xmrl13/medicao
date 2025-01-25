package controller;

import dto.PlaceDTO;
import dto.PlaceRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.PlaceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaceControllerTest {

    private final PlaceService placeService = mock(PlaceService.class);
    private final PlaceController placeController = new PlaceController(placeService);

    @Test
    void testCreatePlaceShouldReturnCreated() {
        // Arrange
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");
        String token = "Bearer valid-token";

        when(placeService.createPlace(eq(placeDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(201).body("Bacia criada com sucesso")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.createPlace(placeDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("Bacia criada com sucesso");
    }

    @Test
    void testDeletePlaceShouldReturnOk() {
        // Arrange
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");
        String token = "Bearer valid-token";

        when(placeService.deletePlace(eq(placeDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia deletada com sucesso")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.deletePlace(placeDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Bacia deletada com sucesso");
    }

    @Test
    void testExistByNameAndProjectShouldReturnFound() {
        // Arrange
        PlaceRequestDTO placeRequestDTO = new PlaceRequestDTO("Test Place", "Test Contract");
        String token = "Bearer valid-token";

        when(placeService.existsByNameAndProjectContract(eq(placeRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia encontrada")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.existByNameAndProject(placeRequestDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Bacia encontrada");
    }

    @Test
    void testCreatePlaceShouldReturnConflictWhenPlaceExists() {
        // Arrange
        PlaceDTO placeDTO = new PlaceDTO("Existing Place", "Existing Contract");
        String token = "Bearer valid-token";

        when(placeService.createPlace(eq(placeDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(409).body("A bacia já existe para o contrato fornecido")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.createPlace(placeDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(409);
        assertThat(result.getBody()).isEqualTo("A bacia já existe para o contrato fornecido");
    }

    @Test
    void testDeletePlaceShouldReturnNotFoundWhenNotExists() {
        // Arrange
        PlaceDTO placeDTO = new PlaceDTO("Nonexistent Place", "Nonexistent Contract");
        String token = "Bearer valid-token";

        when(placeService.deletePlace(eq(placeDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(404).body("Bacia não encontrada")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.deletePlace(placeDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("Bacia não encontrada");
    }

    @Test
    void testExistByNameAndProjectShouldReturnNotFound() {
        // Arrange
        PlaceRequestDTO placeRequestDTO = new PlaceRequestDTO("Nonexistent Place", "Nonexistent Contract");
        String token = "Bearer valid-token";

        when(placeService.existsByNameAndProjectContract(eq(placeRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(404).body("Bacia não encontrada")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.existByNameAndProject(placeRequestDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("Bacia não encontrada");
    }

    @Test
    void testCreatePlaceShouldHandleInternalServerError() {
        // Arrange
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");
        String token = "Bearer valid-token";

        when(placeService.createPlace(eq(placeDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(500).body("Erro interno do servidor")));

        // Act
        Mono<ResponseEntity<String>> response = placeController.createPlace(placeDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(500);
        assertThat(result.getBody()).isEqualTo("Erro interno do servidor");
    }
}
