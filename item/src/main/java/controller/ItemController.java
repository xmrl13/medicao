package controller;

import dto.ItemDTO;
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
    public Mono<ResponseEntity<String>> createOne(@RequestBody ItemDTO itemDTO, @RequestHeader("Authorization") String token) {
        return itemService.createItem(itemDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteItem(@RequestBody ItemDTO itemDTO, @RequestHeader("Authorization") String token) {
        return itemService.deleteItem(itemDTO, token);
    }

    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existItem(@RequestBody ItemRequestDTO itemRequestDTO, @RequestHeader("Authorization") String token) {
        System.out.println("executei no controller de project");
        return itemService.existsByNameAndUnit(itemRequestDTO, token);
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
