package service;

import client.ItemClient;
import dto.ItemDTO;
import dto.ItemRequestDTO;
import model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import repository.ItemRepository;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ItemServiceTest {

    @Mock
    private ItemClient itemClient;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createItemShouldHandleUnexpectedErrors() {
        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "createItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenThrow(new RuntimeException("Unexpected error"));

        StepVerifier.create(itemService.createItem(itemDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .verifyComplete();
    }

    @Test
    void deleteItemShouldHandleUnexpectedErrors() {
        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "deleteItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenThrow(new RuntimeException("Unexpected error"));

        StepVerifier.create(itemService.deleteItem(itemDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).contains("Erro ao processar a exclusão do item: Unexpected error");
                })
                .verifyComplete();
    }

    @Test
    void existsByNameAndUnitShouldHandleUnexpectedErrors() {
        String token = "valid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("item1", "kg");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(requestDTO.getName(), requestDTO.getUnit()))
                .thenThrow(new RuntimeException("Unexpected error"));

        StepVerifier.create(itemService.existsByNameAndUnit(requestDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).contains("Erro ao verificar a existência do item: Unexpected error");
                })
                .verifyComplete();
    }

    @Test
    void createItemShouldHandlePermissionCheckErrors() {
        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "createItem"))
                .thenReturn(Mono.error(new RuntimeException("Permission service error")));

        StepVerifier.create(itemService.createItem(itemDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .verifyComplete();
    }

    @Test
    void deleteItemShouldHandlePermissionCheckErrors() {
        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "deleteItem"))
                .thenReturn(Mono.error(new RuntimeException("Permission service error")));

        StepVerifier.create(itemService.deleteItem(itemDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).contains("Erro ao processar a exclusão do item: Permission service error");
                })
                .verifyComplete();
    }

    @Test
    void existsByNameAndUnitShouldHandlePermissionCheckErrors() {
        String token = "valid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("item1", "kg");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.error(new RuntimeException("Permission service error")));

        StepVerifier.create(itemService.existsByNameAndUnit(requestDTO, token))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).contains("Erro ao verificar a existência do item: Permission service error");
                })
                .verifyComplete();
    }
}
