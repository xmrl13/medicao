package repository;


import model.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    Mono<Item> findByNameAndUnit(String name, String unit);

}