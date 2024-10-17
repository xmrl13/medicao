package placeitem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "place_itens", uniqueConstraints = {@UniqueConstraint(columnNames = {"place_id", "item_id"})}
        , indexes = {@Index(name = "idx_place_id_item_id", columnList = "place_id, item_id")})
public class PlaceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "place_id", unique = true, nullable = false)
    private Long placeId;

    @Setter
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Setter
    @Column(name = "predict_valeu", precision = 20, scale = 2, nullable = false)
    private BigDecimal predictValue;

    @Setter
    @Column(name = "accumulated_value", precision = 20, scale = 2)
    private BigDecimal accumulatedValue;

    public PlaceItem() {
    }


    public PlaceItem(Long placeId, Long itemId, BigDecimal predictValue) {
        this.placeId = placeId;
        this.itemId = itemId;
        this.predictValue = predictValue;
    }
}