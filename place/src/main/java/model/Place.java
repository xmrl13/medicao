package place.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "places",schema = "app", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "project_contract"})}
, indexes = @Index(name = "idx_name_project_contrac", columnList = "name, project_contract"))
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Setter
    @Column(name = "project_contract", nullable = false)
    @NotBlank
    private String projectContract;

    public Place() {
    }

    public Place(String name, String projectContract) {
        this.name = name;
        this.projectContract = projectContract;
    }
}
