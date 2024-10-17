package controller;

import dto.ItemRequestDTO;
import service.ItemService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("api/itens")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/create/one")
    public ResponseEntity<ItemRequestDTO> createOne(@Valid @RequestBody ItemRequestDTO itemRequestDTO) {

        itemService.createItem(itemRequestDTO);
        return ResponseEntity.ok().body(itemRequestDTO);
    }

    @GetMapping("/getidbynameandunit/{name}/{unit}")
    public ResponseEntity<?> getIdByNameAndUnit(@PathVariable String name, @PathVariable String unit) {

        String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String decodedUnit = URLDecoder.decode(unit, StandardCharsets.UTF_8);

        Optional<Long> id = itemService.getIdByNameAndUnit(decodedName, decodedUnit);
        System.out.println("Passei aqui");
        return ResponseEntity.ok(id);
    }

    @GetMapping("/existsbyid")
    public boolean existsById(@PathVariable Long id) {
        return itemService.existsById(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> BadRequestException(BadRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> Exception(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
