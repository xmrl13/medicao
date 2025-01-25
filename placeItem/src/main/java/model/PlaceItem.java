package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Table("place_itens")
public class PlaceItem {

    @Id
    private Long id;

    @Setter
    @Column("place_name")
    private String placeName;

    @Setter
    @Column("project_contract")
    private String projectContract;

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

    public PlaceItem() {
    }
}