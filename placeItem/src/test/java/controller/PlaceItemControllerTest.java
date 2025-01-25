package controller;

import dto.PlaceItemRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.PlaceItemService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PlaceItemControllerTest {

    private PlaceItemService placeItemService;
    private PlaceItemController placeItemController;

    @BeforeEach
    void setUp() {
        placeItemService = Mockito.mock(PlaceItemService.class);
        placeItemController = new PlaceItemController(placeItemService);
    }

    @Test
    @DisplayName("Deve criar um PlaceItem com sucesso")
    void createPlaceItem_Success() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer valid-token";

        when(placeItemService.createPlaceItem(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("PlaceItem criado com sucesso")));

        Mono<ResponseEntity<String>> response = placeItemController.createPlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("PlaceItem criado com sucesso", result.getBody());
        });

        verify(placeItemService, times(1)).createPlaceItem(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar um PlaceItem sem permissão")
    void createPlaceItem_Forbidden() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer invalid-token";

        when(placeItemService.createPlaceItem(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para realizar essa ação")));

        Mono<ResponseEntity<String>> response = placeItemController.createPlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertEquals("Sem permissão para realizar essa ação", result.getBody());
        });

        verify(placeItemService, times(1)).createPlaceItem(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve deletar um PlaceItem com sucesso")
    void deletePlaceItem_Success() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer valid-token";

        when(placeItemService.deletePlaceItem(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("PlaceItem deletado com sucesso")));

        Mono<ResponseEntity<String>> response = placeItemController.deletePlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("PlaceItem deletado com sucesso", result.getBody());
        });

        verify(placeItemService, times(1)).deletePlaceItem(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao deletar um PlaceItem inexistente")
    void deletePlaceItem_NotFound() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer valid-token";

        when(placeItemService.deletePlaceItem(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("PlaceItem não encontrado")));

        Mono<ResponseEntity<String>> response = placeItemController.deletePlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("PlaceItem não encontrado", result.getBody());
        });

        verify(placeItemService, times(1)).deletePlaceItem(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve verificar se um PlaceItem existe")
    void existPlaceItem_Success() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer valid-token";

        when(placeItemService.existsByNameAndContract(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("PlaceItem encontrado")));

        Mono<ResponseEntity<String>> response = placeItemController.existPlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("PlaceItem encontrado", result.getBody());
        });

        verify(placeItemService, times(1)).existsByNameAndContract(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao verificar PlaceItem inexistente")
    void existPlaceItem_NotFound() {
        PlaceItemRequestDTO placeItemRequestDTO = new PlaceItemRequestDTO();
        String token = "Bearer valid-token";

        when(placeItemService.existsByNameAndContract(eq(placeItemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("PlaceItem não encontrado")));

        Mono<ResponseEntity<String>> response = placeItemController.existPlaceItem(placeItemRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("PlaceItem não encontrado", result.getBody());
        });

        verify(placeItemService, times(1)).existsByNameAndContract(eq(placeItemRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve buscar todos os PlaceItens por token")
    void getAllPlaceItensByToken_Success() {
        String token = "Bearer valid-token";

        when(placeItemService.getAllPlaceItensByToken(eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Lista de PlaceItens retornada")));

        Mono<ResponseEntity<?>> response = placeItemController.getAllPlacesItensByToken(token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Lista de PlaceItens retornada", result.getBody());
        });

        verify(placeItemService, times(1)).getAllPlaceItensByToken(eq(token));
    }
}
