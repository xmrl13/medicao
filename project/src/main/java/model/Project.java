package model;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.List;
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
    @Column("user_email")
    private List<String> userEmail;

    public Project() {
    }

    public Project(@NotBlank(message = "O nome da obra é obrigatório.") @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES") String name, @NotBlank(message = "O contrato é obrigatório.") String contract, @DecimalMin(value = "0.0", inclusive = false, message = "O orçamento deve ser maior que zero.") BigDecimal budget) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) && Objects.equals(contract, project.contract) && Objects.equals(budget, project.budget) && Objects.equals(userEmail, project.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, contract, budget, userEmail);
    }
}
