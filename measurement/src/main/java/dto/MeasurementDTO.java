package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class MeasurementDTO {

    @Getter
    @Setter
    @NotBlank
    private String projectContract;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;


    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{4})$", message = "Formato inválido para yearMonth. Deve ser MM/YYYY.")
    private String yearMonth;

    public MeasurementDTO(String projectContract, LocalDate startDate, LocalDate endDate, String yearMonth) {
        this.projectContract = projectContract;
        this.startDate = startDate;
        this.endDate = endDate;
        this.yearMonth = yearMonth;
    }

    public MeasurementDTO() {
    }

    public MeasurementDTO(String project1, String s, Object endDate, Object yearMonth) {
    }
}
