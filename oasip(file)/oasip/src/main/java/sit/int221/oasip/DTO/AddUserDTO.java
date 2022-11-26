package sit.int221.oasip.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.oasip.enums.Roles;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddUserDTO {
    private int id;
    private String name;
    private String email;
    private Roles role;

    @NotNull(message = "Password is not null")
    @Size(min = 1 , max = 14 , message = "Password is not empty and must between 1 - 14")
    @Column(name = "password", nullable = false , length = 100 )
    private String password;

}