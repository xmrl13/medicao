package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("places")
public class Place {

    @Id
    private Long id;

    @Setter
    @Column("name")
    private String name;

    @Setter
    @Column("project_contract")
    private String projectContract;

    public Place() {
    }

    public Place(String name, String projectContract) {
        this.name = name;
        this.projectContract = projectContract;
    }
}
