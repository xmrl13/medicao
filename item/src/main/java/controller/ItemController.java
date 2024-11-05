package controller;

import dto.ItemRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.ItemService;

@RestController
@RequestMapping("api/itens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create/one")
    public Mono<ResponseEntity<?>> createOne(@RequestBody ItemRequestDTO itemRequestDTO, @RequestHeader("Authorization") String token) {
        return itemService.createItem(itemRequestDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<?>> deleteItem(@RequestBody ItemRequestDTO itemRequestDTO, @RequestHeader("Authorization") String token) {
        return itemService.deleteItem(itemRequestDTO, token);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> Exception(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
