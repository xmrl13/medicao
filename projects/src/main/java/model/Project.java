package project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    @NotBlank
    private String name;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    @NotBlank
    private String contract;

    @Getter
    @Setter
    @Column(precision = 30, scale = 2, nullable = false)
    private BigDecimal budget;

    @Getter
    @Setter
    @Column(name = "users_id")
    private Long userId;

    public Project() {
    }

    public Project(String name, String contract, BigDecimal budget) {
        this.name = name;
        this.contract = contract;
        this.budget = budget;
    }
}
