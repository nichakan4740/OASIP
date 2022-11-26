package sit.int221.oasip.DTO;

import lombok.Getter;
import lombok.Setter;
import sit.int221.oasip.enums.Roles;

@Setter
@Getter
public class ResponseCreateTokenDTO {
    private String message;
    private String username;
//    private String email;
    private Roles role;
    private String accessToken;
    private String refreshToken;

    public ResponseCreateTokenDTO(String message, String username , Roles role , String accessToken , String refreshToken) {
        this.message = message;
        this.username = username;
//        this.email = email;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
