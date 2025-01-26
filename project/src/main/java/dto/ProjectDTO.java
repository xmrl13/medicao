package dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProjectDTO {

    @NotBlank(message = "O nome da obra é obrigatório.")
    private String name;

    @NotBlank(message = "O contrato é obrigatório.")
    private String contract;

    @NotNull
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

    public ProjectDTO(String name, String contract, double v) {
    }
}
