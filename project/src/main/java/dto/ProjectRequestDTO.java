package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectRequestDTO {

    private String contract;
    private String userEmail;


    public ProjectRequestDTO(String contract, String userEmail) {
        this.contract = contract;
        this.userEmail = userEmail;
    }
}
