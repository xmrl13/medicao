package service;

import client.PlaceItemClient;
import dto.PlaceItemRequestDTO;
import model.PlaceItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import repository.PlaceItemRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class PlaceItemServiceTest {

    @Mock
    private PlaceItemRepository placeItemRepository;

    @Mock
    private PlaceItemClient placeItemClient;

    @InjectMocks
    private PlaceItemService placeItemService;

    private PlaceItemRequestDTO placeItemRequestDTO;
    private String token;

    @BeforeEach
    void setup() {
        token = "test-token";
        placeItemRequestDTO = new PlaceItemRequestDTO();
        placeItemRequestDTO.setItemName("Item-1");
        placeItemRequestDTO.setItemUnit("kg");
        placeItemRequestDTO.setPlaceName("Place-1");
        placeItemRequestDTO.setProjectContract("Contract-123");
        placeItemRequestDTO.setPredictedValue(BigDecimal.valueOf(100.0));
    }

    @Test
    void createPlaceItem_AcaoNaoEncontrada() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Ação não encontrada")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Ação não encontrada: createPlaceItem", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_SemPermissao() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FORBIDDEN, response.getStatusCode());
                    assertEquals("Sem permissão para realizar essa ação", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_ErroAoVerificarPermissao() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Erro genérico")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertEquals("Erro ao verificar permissão: Erro genérico", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_BaciaNaoEncontrada() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Bacia não encontrada")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Bacia não encontrada, nome: Place-1 contrato: Contract-123", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_ItemNaoEncontrado() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia encontrada")));

        when(placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Item não encontrado")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Item não encontrado, nome: Item-1 unidade: kg", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_ItemJaExiste() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia encontrada")));

        when(placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()))
                .thenReturn(Mono.just(ResponseEntity.ok("Item encontrado")));

        PlaceItem existingPlaceItem = new PlaceItem();
        existingPlaceItem.setItemName("Item-1");
        existingPlaceItem.setItemUnit("kg");
        existingPlaceItem.setPlaceName("Place-1");
        existingPlaceItem.setProjectContract("Contract-123");

        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                "Item-1", "kg", "Place-1", "Contract-123"
        )).thenReturn(Mono.just(existingPlaceItem));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CONFLICT, response.getStatusCode());
                    assertEquals("Já existe", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_CriadoComSucesso() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia encontrada")));

        when(placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()))
                .thenReturn(Mono.just(ResponseEntity.ok("Item encontrado")));

        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                "Item-1", "kg", "Place-1", "Contract-123"
        )).thenReturn(Mono.empty());

        when(placeItemRepository.save(any(PlaceItem.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CREATED, response.getStatusCode());
                    assertEquals("", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deletePlaceItem_AcaoNaoEncontrada() {
        when(placeItemClient.hasPermission(token, "deletePlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Ação não encontrada")));

        Mono<ResponseEntity<String>> result = placeItemService.deletePlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Ação não encontrada: deletePlaceItem", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deletePlaceItem_Sucesso() {
        when(placeItemClient.hasPermission(token, "deletePlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        PlaceItem existingPlaceItem = new PlaceItem();
        existingPlaceItem.setItemName("Item-1");
        existingPlaceItem.setItemUnit("kg");
        existingPlaceItem.setPlaceName("Place-1");
        existingPlaceItem.setProjectContract("Contract-123");

        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                "Item-1", "kg", "Place-1", "Contract-123"
        )).thenReturn(Mono.just(existingPlaceItem));

        when(placeItemRepository.delete(existingPlaceItem)).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = placeItemService.deletePlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Item Deletado com sucesso", response.getBody());
                })
                .verifyComplete();
    }
}
