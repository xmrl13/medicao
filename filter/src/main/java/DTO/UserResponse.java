package DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private String email;
    private String role;
    private String password;

    public boolean isValid() {
        return true;
    }
}

