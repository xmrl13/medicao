package measurementplaceitem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "measurement_place_itens")
public class MeasurementPlaceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Long measurementId;

    @Setter
    @Column(nullable = false)
    private Long placeItemId;

    @Setter
    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal measuredValue;

    public MeasurementPlaceItem(Long measurementId, Long placeItemId, BigDecimal measuredValue) {
        this.measurementId = measurementId;
        this.placeItemId = placeItemId;
        this.measuredValue = measuredValue;
    }
}
