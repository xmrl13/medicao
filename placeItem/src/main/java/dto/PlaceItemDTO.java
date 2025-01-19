package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceItemDTO {

    private Long placeId;

    private Long itemId;

    @NotBlank(message = "Valor previsto nao pode ser vazio")
    private BigDecimal predictedValue;

    public PlaceItemDTO(Long placeId, Long itemId, BigDecimal predictedValue) {
        this.placeId = placeId;
        this.itemId = itemId;
        this.predictedValue = predictedValue;
    }
}
