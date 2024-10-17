package measurement.dto;

import placeitem.dto.PlaceItemRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Setter
@Getter
public class MeasurementDTO {

    @Getter
    @Setter
    @NotBlank
    private String projectContract;

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    @NotNull
    private YearMonth yearMonth;

    private List<PlaceItemRequestDTO> itensMedidos;

    public MeasurementDTO(String projectContract, LocalDate start, LocalDate end, YearMonth yearMonth) {
        this.projectContract = projectContract;
        this.start = start;
        this.end = end;
        this.yearMonth = yearMonth;
    }
}
