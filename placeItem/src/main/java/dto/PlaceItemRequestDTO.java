package dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceItemRequestDTO {

    private String itemName;

    private String itemUnit;

    private String placeName;

    private String projectContract;

    @NotNull(message = "Valor previsto nao pode ser vazio")
    private BigDecimal predictedValue;
}