package measurementplaceitem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MeasurementPlaceItemDTO {

    @NotNull(message = "ID da medição obrigatório")
    private Long measurementId;

    @NotNull(message = "ID da da bacia item obrigatorio")
    private Long placeItemId;

    @NotNull(message = "Valor medido obrigatório")
    private BigDecimal measuredValue;
}
