package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceRequestDTO {

    private String name;

    private String contract;

    public PlaceRequestDTO(String testPlace, String testContract) {
    }
}
