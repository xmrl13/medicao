package dto;

import lombok.Getter;
import lombok.Setter;
import model.MeasurementPlaceItem;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class MeasurementPlaceItemDTO {

    private Long id;
    private String projectContract;
    private LocalDate startDate;
    private LocalDate endDate;
    private String yearMonth;
    private String placeName;
    private String itemName;
    private String itemUnit;
    private BigDecimal predictedValue;
    private BigDecimal accumulatedValue;

    public MeasurementPlaceItemDTO() {
    }
}
