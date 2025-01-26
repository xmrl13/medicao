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
        token = "Bearer token-teste";
        placeItemRequestDTO = new PlaceItemRequestDTO();
        placeItemRequestDTO.setPlaceName("Bacia Teste");
        placeItemRequestDTO.setProjectContract("CONTRATO-123");
        placeItemRequestDTO.setItemName("Item Teste");
        placeItemRequestDTO.setItemUnit("Unidade");
        placeItemRequestDTO.setPredictedValue(BigDecimal.valueOf(100.00));
    }

    @Test
    void createPlaceItem_PermissaoNegada() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        Mono<ResponseEntity<String>> result = placeItemService.createPlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response ->
                        assertEquals(FORBIDDEN, response.getStatusCode())
                )
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
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Bacia não encontrada, nome: Bacia Teste contrato: CONTRATO-123", response.getBody());
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
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Item não encontrado, nome: Item Teste unidade: Unidade", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createPlaceItem_ConflitoAoCriar() {
        when(placeItemClient.hasPermission(token, "createPlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Bacia encontrada")));

        when(placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()))
                .thenReturn(Mono.just(ResponseEntity.ok("Item encontrado")));

        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                placeItemRequestDTO.getItemName(),
                placeItemRequestDTO.getItemUnit(),
                placeItemRequestDTO.getPlaceName(),
                placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(new PlaceItem()));

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
                placeItemRequestDTO.getItemName(),
                placeItemRequestDTO.getItemUnit(),
                placeItemRequestDTO.getPlaceName(),
                placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.empty());

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
    void deletePlaceItem_NaoEncontrado() {
        when(placeItemClient.hasPermission(token, "deletePlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                placeItemRequestDTO.getItemName(),
                placeItemRequestDTO.getItemUnit(),
                placeItemRequestDTO.getPlaceName(),
                placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = placeItemService.deletePlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Não encontrado", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deletePlaceItem_Sucesso() {
        when(placeItemClient.hasPermission(token, "deletePlaceItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        PlaceItem existingPlaceItem = new PlaceItem();
        when(placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
                placeItemRequestDTO.getItemName(),
                placeItemRequestDTO.getItemUnit(),
                placeItemRequestDTO.getPlaceName(),
                placeItemRequestDTO.getProjectContract()))
                .thenReturn(Mono.just(existingPlaceItem));

        when(placeItemRepository.delete(existingPlaceItem))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = placeItemService.deletePlaceItem(placeItemRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Item Deletado com sucesso", response.getBody());
                })
                .verifyComplete();
    }
}
