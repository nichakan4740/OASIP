package sit.int221.oasip.DTO;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseSaveFileDTO {
    private String message;

    public ResponseSaveFileDTO(String message) {
        this.message = message;
    }

}
