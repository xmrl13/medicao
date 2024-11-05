package service;

import client.ItemClient;
import dto.ItemRequestDTO;
import model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    private ItemClient itemClient;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<ResponseEntity<?>> createItem(ItemRequestDTO itemRequestDTO, String token) {
        String action = "createItem";

        return itemClient.hasPermission(token, action)
                .flatMap(hasPermission -> {
                    if (hasPermission == null) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Permissão não existente: " + action));
                    }
                    if (!hasPermission) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão"));
                    }
                    return itemRepository.findByNameAndUnit(itemRequestDTO.getName(), itemRequestDTO.getUnit())
                            .flatMap(existingItem ->
                                    Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                            .body("Item já cadastrado")))
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        Item item = new Item();
                                        item.setName(itemRequestDTO.getName());
                                        item.setUnit(itemRequestDTO.getUnit());
                                        return itemRepository.save(item)
                                                .map(savedItem -> ResponseEntity.status(HttpStatus.CREATED)
                                                        .body("Item criado com sucesso"));
                                    })
                            );
                });
    }


    public Mono<ResponseEntity<?>> deleteItem(ItemRequestDTO itemRequestDTO, String token) {

        return itemClient.hasPermission(token, "deleteUser")
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão"));
                    }

                    return itemRepository.findByNameAndUnit(itemRequestDTO.getName(), itemRequestDTO.getUnit())
                            .flatMap(existingItem ->
                                    itemRepository.delete(existingItem)
                                            .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deletado com sucesso")))
                            )
                            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado")));
                });
    }

    public Mono<ResponseEntity<Object>> existsByNameAndUnit(ItemRequestDTO itemRequestDTO) {
        return itemRepository.findByNameAndUnit(itemRequestDTO.getName(), itemRequestDTO.getUnit())
                .flatMap(existingItem ->
                        Mono.just(ResponseEntity.status(HttpStatus.OK)
                                .body((Object) "Item já cadastrado")))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("Item nao encontrado"))));
    }
}
