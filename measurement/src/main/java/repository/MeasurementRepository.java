package measurement.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import measurement.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.YearMonth;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    boolean existsByYearMonth(@NotNull YearMonth competencia);

    boolean existsByProjectContractAndStartLessThanEqualAndEndGreaterThanEqual(@NotBlank String projectContract, @NotNull LocalDate end, @NotNull LocalDate start);
}

