package client;

import dto.ItemRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import service.ItemService;

@Component
@RestController
@RequestMapping("api/itens")
public class ItemClient {

    private final WebClient webClient;
    private final ItemService itemService;

    public ItemClient(WebClient.Builder webClientBuilder, @Lazy ItemService itemService) {
        this.webClient = webClientBuilder.baseUrl("lb://user").build();
        this.itemService = itemService;
    }

    public Mono<Boolean> hasPermission(String token, String action) {
        return webClient.post()
                .uri("api/users/has-permission/{token}/{action}", token, action)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }


    @GetMapping("/existsbynameandunit")
    public Mono<ResponseEntity<Object>> existsByNameAndUnit(@RequestBody ItemRequestDTO itemRequestDTO) {
        return itemService.existsByNameAndUnit(itemRequestDTO);
    }
}
