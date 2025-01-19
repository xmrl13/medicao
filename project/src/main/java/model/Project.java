package model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Table("projects")
public class Project {

    @Id
    private Long id;

    @Setter
    @Column("name")
    private String name;

    @Setter
    @Column("contract")
    private String contract;

    @Setter
    private BigDecimal budget;

    @Setter
    @Column("user_id")
    private Long userId;

    public Project() {
    }

    public Project(String name, String contract, BigDecimal budget) {
        this.name = name;
        this.contract = contract;
        this.budget = budget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && Objects.equals(contract, project.contract) && Objects.equals(budget, project.budget) && Objects.equals(userId, project.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contract, budget, userId);
    }
}
