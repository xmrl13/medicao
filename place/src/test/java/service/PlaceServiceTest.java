package service;

import client.PlaceClient;
import dto.PlaceDTO;
import dto.PlaceRequestDTO;
import model.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import repository.PlaceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceClient placeClient;

    @InjectMocks
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar Place quando há permissão e não existe registro anterior")
    void testCreatePlace_ShouldReturnCreated() {
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");

        when(placeClient.hasPermission(any(), eq("createPlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract()))
                .thenReturn(Mono.empty());

        when(placeRepository.save(any(Place.class)))
                .thenReturn(Mono.just(new Place("Test Place", "Test Contract")));

        StepVerifier.create(placeService.createPlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(CREATED, response.getStatusCode());
                    assertEquals("Bacia criada com sucesso", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar CONFLICT quando o Place já existe")
    void testCreatePlace_ShouldReturnConflictWhenAlreadyExists() {
        PlaceDTO placeDTO = new PlaceDTO("Existing Place", "Existing Contract");

        when(placeClient.hasPermission(any(), eq("createPlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract()))
                .thenReturn(Mono.just(new Place("Existing Place", "Existing Contract")));

        StepVerifier.create(placeService.createPlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(CONFLICT, response.getStatusCode());
                    assertEquals("A bacia: Existing Place já existe para o contrato: Existing Contract", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar FORBIDDEN quando não há permissão")
    void testCreatePlace_ShouldReturnForbidden() {
        PlaceDTO placeDTO = new PlaceDTO("New Place", "New Contract");

        when(placeClient.hasPermission(any(), eq("createPlace")))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        StepVerifier.create(placeService.createPlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(FORBIDDEN, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar FAILED_DEPENDENCY quando PlaceClient falhar")
    void testCreatePlace_ShouldReturnFailedDependency() {
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");

        when(placeClient.hasPermission(any(), eq("createPlace")))
                .thenReturn(Mono.just(ResponseEntity.status(SERVICE_UNAVAILABLE).body("Dependência indisponível")));

        StepVerifier.create(placeService.createPlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(FAILED_DEPENDENCY, response.getStatusCode());
                    assertEquals("Uma dependência falhou.", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar INTERNAL_SERVER_ERROR quando ocorrer uma exceção inesperada no createPlace")
    void testCreatePlace_ShouldReturnInternalServerErrorOnUnexpectedException() {
        PlaceDTO placeDTO = new PlaceDTO("Test Place", "Test Contract");

        when(placeClient.hasPermission(any(), eq("createPlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(any(), any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        StepVerifier.create(placeService.createPlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve deletar Place com sucesso")
    void testDeletePlace_ShouldReturnOk() {
        PlaceDTO placeDTO = new PlaceDTO("Deletable Place", "Deletable Contract");

        when(placeClient.hasPermission(any(), eq("deletePlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract()))
                .thenReturn(Mono.just(new Place("Deletable Place", "Deletable Contract")));

        when(placeRepository.delete(any(Place.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(placeService.deletePlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Bacia deletada com sucesso", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar NO_CONTENT quando o Place não existir no delete")
    void testDeletePlace_ShouldReturnNoContent() {
        PlaceDTO placeDTO = new PlaceDTO("Nonexistent Place", "Nonexistent Contract");

        when(placeClient.hasPermission(any(), eq("deletePlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract()))
                .thenReturn(Mono.empty());

        StepVerifier.create(placeService.deletePlace(placeDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Bacia não encontrada com o nome e contrato de projeto fornecidos", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar OK quando Place existir no existsByNameAndProjectContract")
    void testExistsByNameAndProjectContract_ShouldReturnOK() {
        PlaceRequestDTO requestDTO = new PlaceRequestDTO("Existing Place", "Existing Contract");

        when(placeClient.hasPermission(any(), eq("existPlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(requestDTO.getName(), requestDTO.getContract()))
                .thenReturn(Mono.just(new Place("Existing Place", "Existing Contract")));

        StepVerifier.create(placeService.existsByNameAndProjectContract(requestDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Bacia encontrada", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar NO_CONTENT quando Place não existir no existsByNameAndProjectContract")
    void testExistsByNameAndProjectContract_ShouldReturnNoContent() {
        PlaceRequestDTO requestDTO = new PlaceRequestDTO("Nonexistent Place", "Nonexistent Contract");

        when(placeClient.hasPermission(any(), eq("existPlace")))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission Granted")));

        when(placeRepository.findByNameAndProjectContract(requestDTO.getName(), requestDTO.getContract()))
                .thenReturn(Mono.empty());

        StepVerifier.create(placeService.existsByNameAndProjectContract(requestDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Bacia não encontrada", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar FAILED_DEPENDENCY quando PlaceClient falhar no existsByNameAndProjectContract")
    void testExistsByNameAndProjectContract_ShouldReturnFailedDependency() {
        PlaceRequestDTO requestDTO = new PlaceRequestDTO("Test Place", "Test Contract");

        when(placeClient.hasPermission(any(), eq("existPlace")))
                .thenReturn(Mono.just(ResponseEntity.status(SERVICE_UNAVAILABLE).body("Dependência indisponível")));

        StepVerifier.create(placeService.existsByNameAndProjectContract(requestDTO, "test-token"))
                .assertNext(response -> {
                    assertEquals(FAILED_DEPENDENCY, response.getStatusCode());
                    assertEquals("Uma dependência falhou.", response.getBody());
                })
                .verifyComplete();
    }
}
