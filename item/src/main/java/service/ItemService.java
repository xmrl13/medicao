package service;

import client.ItemClient;
import dto.ItemDTO;
import dto.ItemRequestDTO;
import model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.ItemRepository;

import static org.springframework.http.HttpStatus.*;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemClient itemClient;


    public ItemService(ItemRepository itemRepository, ItemClient client) {
        this.itemClient = client;
        this.itemRepository = itemRepository;
    }

    public Mono<ResponseEntity<String>> createItem(ItemDTO itemDTO, String token) {

        String action = "createItem";

        return itemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit())
                            .flatMap(existingItem ->
                                    Mono.just(ResponseEntity.status(CONFLICT)
                                            .body("O item: " + itemDTO.getName() + " já existe com a unidade: " + itemDTO.getUnit()))
                            )
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        Item item = new Item();
                                        item.setName(itemDTO.getName());
                                        item.setUnit(itemDTO.getUnit());

                                        return itemRepository.save(item)
                                                .map(savedItem -> ResponseEntity.status(CREATED)
                                                        .body("Item criado com sucesso"));
                                    })
                            );
                });
    }

    public Mono<ResponseEntity<String>> deleteItem(ItemDTO itemDTO, String token) {
        String action = "deleteItem";

        return itemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit())
                            .flatMap(existingItem ->
                                    itemRepository.delete(existingItem)
                                            .then(Mono.just(ResponseEntity.status(OK)
                                                    .body("Item deletado com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NO_CONTENT)
                                    .body("Item não encontrado com o nome e unidade fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão do item: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByNameAndUnit(ItemRequestDTO itemRequestDTO, String token) {
        String action = "existItem";

        return itemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return itemRepository.findByNameAndUnit(itemRequestDTO.getName(), itemRequestDTO.getUnit())
                            .flatMap(existingItem ->
                                    Mono.just(ResponseEntity.status(OK)
                                            .body("Item encontrado")))
                            .switchIfEmpty(
                                    Mono.just(ResponseEntity.status(NO_CONTENT)
                                            .body("Item não encontrado")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência do item: " + error.getMessage())));
    }
}
