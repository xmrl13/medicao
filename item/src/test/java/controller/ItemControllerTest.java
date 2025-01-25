package controller;

import dto.ItemDTO;
import dto.ItemRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.ItemService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemControllerTest {

    private final ItemService itemService = mock(ItemService.class);
    private final ItemController itemController = new ItemController(itemService);

    @Test
    void testCreateOneShouldReturnCreated() {
        // Arrange
        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.createItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(201).body("Item criado com sucesso")));

        // Act
        Mono<ResponseEntity<String>> response = itemController.createOne(itemDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("Item criado com sucesso");
    }

    @Test
    void testDeleteItemShouldReturnOk() {
        // Arrange
        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.deleteItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Item deletado com sucesso")));

        // Act
        Mono<ResponseEntity<String>> response = itemController.deleteItem(itemDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Item deletado com sucesso");
    }

    @Test
    void testExistItemShouldReturnFound() {
        // Arrange
        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.existsByNameAndUnit(eq(itemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Item encontrado")));

        // Act
        Mono<ResponseEntity<String>> response = itemController.existItem(itemRequestDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Item encontrado");
    }

    @Test
    void testCreateOneShouldReturnUnauthorizedWhenTokenMissing() {
        // Arrange
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        // Simule o comportamento do serviço para ausência de token (opcional)
        when(itemService.createItem(eq(itemDTO), eq(null)))
                .thenThrow(new IllegalArgumentException("Authorization token is required"));

        // Act & Assert
        try {
            itemController.createOne(itemDTO, null).block();
        } catch (Exception e) {
            // Assert
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).contains("Authorization token is required");
        }
    }


    @Test
    void testDeleteItemShouldReturnBadRequestWhenServiceFails() {
        // Arrange
        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.deleteItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.badRequest().body("Erro ao deletar item")));

        // Act
        Mono<ResponseEntity<String>> response = itemController.deleteItem(itemDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("Erro ao deletar item");
    }

    @Test
    void testExistItemShouldReturnNotFound() {
        // Arrange
        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.existsByNameAndUnit(eq(itemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(404).body("Item não encontrado")));

        // Act
        Mono<ResponseEntity<String>> response = itemController.existItem(itemRequestDTO, token);

        // Assert
        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("Item não encontrado");
    }
}
