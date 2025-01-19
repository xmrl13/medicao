package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Table("measurements_places_itens")
public class MeasurementPlaceItem {

    @Id
    private Long id;

    @Column("project_contract")
    private String projectContract;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("year_month")
    private String yearMonth;

    @Setter
    @Column("place_name")
    private String placeName;

    @Setter
    @Column("item_name")
    private String itemName;

    @Setter
    @Column("item_unit")
    private String itemUnit;

    @Setter
    @Column("predicted_value")
    private BigDecimal predictedValue;

    @Setter
    @Column("accumulated_value")
    private BigDecimal accumulatedValue;

    public MeasurementPlaceItem() {
    }
}
