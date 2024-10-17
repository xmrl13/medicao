package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "itens", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "unit"})}
, indexes = {@Index(name = "idx_name_unit", columnList = "name, unit")})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Setter
    @Column(name = "unit", nullable = false)
    @NotBlank
    private String unit;

    public Item() {
    }

    public Item(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
}
