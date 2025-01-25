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
    void createItemShouldCreateItemWhenTokenIsValid() {

        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "createItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenReturn(Mono.empty());

        when(itemRepository.save(any()))
                .thenReturn(Mono.just(new Item("item1", "kg")));

        Mono<ResponseEntity<String>> response = itemService.createItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo("Item criado com sucesso");

        verify(itemClient).hasPermission(token, "createItem");
        verify(itemRepository).findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit());
        verify(itemRepository).save(any());
    }

    @Test
    void createItemShouldReturnForbiddenWhenPermissionDenied() {

        String token = "invalid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "createItem"))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied")));

        Mono<ResponseEntity<String>> response = itemService.createItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo("Sem permissão para realizar essa ação");

        verify(itemClient).hasPermission(token, "createItem");
        verifyNoInteractions(itemRepository);
    }

    @Test
    void createItemShouldReturnConflictWhenItemExists() {

        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "createItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenReturn(Mono.just(new Item("item1", "kg")));

        Mono<ResponseEntity<String>> response = itemService.createItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody()).isEqualTo("O item: item1 já existe com a unidade: kg");

        verify(itemClient).hasPermission(token, "createItem");
        verify(itemRepository).findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deleteItemShouldDeleteItemWhenTokenIsValid() {

        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "deleteItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenReturn(Mono.just(new Item("item1", "kg")));

        when(itemRepository.delete(any()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = itemService.deleteItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("Item deletado com sucesso");

        verify(itemClient).hasPermission(token, "deleteItem");
        verify(itemRepository).findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit());
        verify(itemRepository).delete(any());
    }

    @Test
    void existsByNameAndUnitShouldReturnOkWhenItemExists() {

        String token = "valid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("item1", "kg");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit("item1", "kg"))
                .thenReturn(Mono.just(new Item("item1", "kg")));

        Mono<ResponseEntity<String>> response = itemService.existsByNameAndUnit(requestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("Item encontrado");

        verify(itemClient).hasPermission(token, "existItem");
        verify(itemRepository).findByNameAndUnit("item1", "kg");
    }

    @Test
    void existsByNameAndUnitShouldReturnNotFoundWhenItemDoesNotExist() {

        String token = "valid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("itemX", "litro");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit("itemX", "litro"))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = itemService.existsByNameAndUnit(requestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isEqualTo("Item não encontrado");

        verify(itemClient).hasPermission(token, "existItem");
        verify(itemRepository).findByNameAndUnit("itemX", "litro");
    }

    @Test
    void existsByNameAndUnitShouldReturnForbiddenWhenPermissionDenied() {

        String token = "invalid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("item1", "kg");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied")));

        Mono<ResponseEntity<String>> response = itemService.existsByNameAndUnit(requestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo("Sem permissão para realizar essa ação");

        verify(itemClient).hasPermission(token, "existItem");
        verifyNoInteractions(itemRepository);
    }

    @Test
    void existsByNameAndUnitShouldReturnInternalServerErrorIfPermissionCheckFails() {

        String token = "valid-token";
        ItemRequestDTO requestDTO = new ItemRequestDTO("item1", "kg");

        when(itemClient.hasPermission(token, "existItem"))
                .thenReturn(Mono.error(new RuntimeException("Some error on permission check")));

        Mono<ResponseEntity<String>> response = itemService.existsByNameAndUnit(requestDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).contains("Erro ao verificar a existência do item: Some error on permission check");

        verify(itemClient).hasPermission(token, "existItem");
        verifyNoInteractions(itemRepository);
    }

    @Test
    void deleteItemShouldReturnNotFoundWhenItemDoesNotExist() {

        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("itemX", "litro");

        when(itemClient.hasPermission(token, "deleteItem"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permission granted")));

        when(itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = itemService.deleteItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isEqualTo("Item não encontrado com o nome e unidade fornecidos");

        verify(itemClient).hasPermission(token, "deleteItem");
        verify(itemRepository).findByNameAndUnit("itemX", "litro");
        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void deleteItemShouldReturnInternalServerErrorWhenPermissionCheckThrowsError() {

        String token = "valid-token";
        ItemDTO itemDTO = new ItemDTO("item1", "kg");

        when(itemClient.hasPermission(token, "deleteItem"))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error in permission check")));

        Mono<ResponseEntity<String>> response = itemService.deleteItem(itemDTO, token);

        ResponseEntity<String> result = response.block();
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).contains("Erro ao processar a exclusão do item: Unexpected error in permission check");

        verify(itemClient).hasPermission(token, "deleteItem");
        verifyNoInteractions(itemRepository);
    }


}
