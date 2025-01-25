package dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProjectDTO {

    @NotBlank(message = "O nome da obra é obrigatório.")
    @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES")
    private String name;

    @NotBlank(message = "O contrato é obrigatório.")
    private String contract;

    @DecimalMin(value = "0.0", inclusive = false, message = "O orçamento deve ser maior que zero.")
    private BigDecimal budget;

    private String userEmail;

    public ProjectDTO() {
    }

    public ProjectDTO(String name, String contract, BigDecimal budget) {
        this.name = name;
        this.contract = contract;
        this.budget = budget;
    }
}
