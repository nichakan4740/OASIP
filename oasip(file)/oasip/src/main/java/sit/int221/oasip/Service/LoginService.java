package sit.int221.oasip.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.int221.oasip.DTO.LoginDTO;
import sit.int221.oasip.DTO.ResponseCreateTokenDTO;
import sit.int221.oasip.Entity.User;
import sit.int221.oasip.Error.ValidationHandler;
import sit.int221.oasip.Repository.UserRepository;
import sit.int221.oasip.config.JwtTokenUtil;

@Service
public class LoginService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

    public ResponseEntity login(LoginDTO userlogin) throws  Exception {
        if (repository.existsByEmail(userlogin.getEmail())) {
            User user = repository.findByEmail(userlogin.getEmail());
            if (argon2PasswordEncoder.matches(userlogin.getPassword(), user.getPassword())) {
//                authentication(userlogin.getEmail() , userlogin.getPassword());

                final UserDetails userDetails = userDetailsService.loadUserByUsername(userlogin.getEmail());
                final String token = jwtTokenUtil.generateToken(userDetails);
                final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                return ResponseEntity.ok(new ResponseCreateTokenDTO("Login Success",user.getName(),user.getRole(),token,refreshToken));

            } else {
                return ValidationHandler.ExceptionError(HttpStatus.UNAUTHORIZED, "Password Incorrect");
            }
        } else {
            return ValidationHandler.ExceptionError(HttpStatus.NOT_FOUND, "A user with the specified email DOES NOT exist");

        }
    }
}

//    private void authentication(String email, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
//    }
