package repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import model.Measurement;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

@Repository
public interface MeasurementRepository extends R2dbcRepository<Measurement, Long> {

    Mono<Object> findByProjectContractAndYearMonth(@NotBlank String projectContract, @NotNull String yearMonth);
}

