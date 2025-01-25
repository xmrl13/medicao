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

        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.createItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(201).body("Item criado com sucesso")));

        Mono<ResponseEntity<String>> response = itemController.createOne(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("Item criado com sucesso");
    }

    @Test
    void testDeleteItemShouldReturnOk() {

        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.deleteItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Item deletado com sucesso")));

        Mono<ResponseEntity<String>> response = itemController.deleteItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Item deletado com sucesso");
    }

    @Test
    void testExistItemShouldReturnFound() {

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.existsByNameAndUnit(eq(itemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Item encontrado")));

        Mono<ResponseEntity<String>> response = itemController.existItem(itemRequestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("Item encontrado");
    }

    @Test
    void testCreateOneShouldReturnUnauthorizedWhenTokenMissing() {

        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemService.createItem(eq(itemDTO), eq(null)))
                .thenThrow(new IllegalArgumentException("Authorization token is required"));

        try {
            itemController.createOne(itemDTO, null).block();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).contains("Authorization token is required");
        }
    }


    @Test
    void testDeleteItemShouldReturnBadRequestWhenServiceFails() {

        ItemDTO itemDTO = new ItemDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.deleteItem(eq(itemDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.badRequest().body("Erro ao deletar item")));

        Mono<ResponseEntity<String>> response = itemController.deleteItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("Erro ao deletar item");
    }

    @Test
    void testExistItemShouldReturnNotFound() {

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("item1", "kg");
        String token = "Bearer valid-token";

        when(itemService.existsByNameAndUnit(eq(itemRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(404).body("Item não encontrado")));

        Mono<ResponseEntity<String>> response = itemController.existItem(itemRequestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("Item não encontrado");
    }
}
