package sit.int221.oasip.Service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasip.Entity.User;
import sit.int221.oasip.Repository.UserRepository;
import sit.int221.oasip.Utils.ListMapper;
import sit.int221.oasip.config.JwtTokenUtil;

import java.util.List;

@Service
public class MatchService {
    @Autowired
    private UserRepository repository;

    public boolean match (String email, String password) {
        List<User> allUser = repository.findAll();

        for (User user : allUser) {
            if (user.getEmail().equals(email.trim())) {
                if (matchPassword(password, user.getPassword())) {
                    throw new ResponseStatusException(HttpStatus.OK , "Password matched");
                }else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password Incorrect");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND , "A user with the specified email DOSE NOT exist");
    }

    public boolean matchPassword(String rawPassword, String encodePassword) {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        boolean validPassword = encoder.matches(rawPassword, encodePassword);
        System.out.println(validPassword);
        return validPassword;
    }
}
