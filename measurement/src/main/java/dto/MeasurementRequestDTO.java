package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MeasurementRequestDTO {

    private String projectContract;

    private LocalDate startDate;

    private LocalDate endDate;

    private String yearMonth;
}
