package dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceItemRequestDTO {

    @NotNull
    private String itemName;

    @NotNull
    private String itemUnit;

    @NotNull
    private String placeName;

    @NotNull
    private String projectContract;

    @NotNull(message = "Valor previsto nao pode ser vazio")
    private BigDecimal predictedValue;
}