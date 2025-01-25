package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceItem placeItem = (PlaceItem) o;
        return Objects.equals(placeName, placeItem.placeName) && Objects.equals(projectContract, placeItem.projectContract) && Objects.equals(itemName, placeItem.itemName) && Objects.equals(itemUnit, placeItem.itemUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeName, projectContract, itemName, itemUnit);
    }
}