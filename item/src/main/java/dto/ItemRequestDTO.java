package dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDTO {

    private String name;

    private String unit;

    public ItemRequestDTO(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
}
