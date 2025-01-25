package repository;

import jakarta.validation.constraints.NotBlank;
import model.Place;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlaceRepository extends R2dbcRepository<Place, Long> {

    Mono<Place> findByNameAndProjectContract(@NotBlank(message = "O nome nao pode ser vazio") String name, @NotBlank(message = "O contrato nao pode ser vazio") String projectContract);
}