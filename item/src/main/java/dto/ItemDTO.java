package dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemDTO {

    private String name;
    private String unit;

    public ItemDTO(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
}
