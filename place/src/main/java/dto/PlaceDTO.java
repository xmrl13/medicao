package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceDTO {

    private Long id;

    @NotBlank(message = "O nome nao pode ser vazio")
    private String name;

    @NotBlank(message = "O contrato nao pode ser vazio")
    private String projectContract;

    public PlaceDTO(String nome, String projectContract) {
        this.name = nome;
        this.projectContract = projectContract;
    }
}
