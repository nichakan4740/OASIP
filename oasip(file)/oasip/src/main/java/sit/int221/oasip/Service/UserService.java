package sit.int221.oasip.Service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasip.DTO.*;
import sit.int221.oasip.Entity.User;
import sit.int221.oasip.Error.ValidationHandler;
import sit.int221.oasip.Repository.UserRepository;
import sit.int221.oasip.Utils.ListMapper;
import sit.int221.oasip.config.JwtTokenUtil;
//import sit.int221.oasip.config.JwtTokenUtil;
//import sit.int221.oasip.model.JwtResponse;

import java.util.List;


@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<UserDTO> getAllUser(String sortBy) {
        List<User> list = repository.findAll(Sort.by(sortBy).ascending());
        return listMapper.mapList(list, UserDTO.class, modelMapper);
    }

    public UserDTO getUserWithId(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User id " + id +
                        "Does Not Exist !!!"
                ));

        return modelMapper.map(user, UserDTO.class);
    }


    public User save(AddUserDTO newUser) {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        newUser.setName(newUser.getName().trim());
        newUser.setEmail(newUser.getEmail().trim());
        newUser.setPassword(newUser.getPassword().trim());

        User user = modelMapper.map(newUser, User.class);

        return repository.saveAndFlush(user);
    }

    public void deleteById(Integer id) {
        repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        repository.deleteById(id);
    }


    public EditUserDTO updateUser(EditUserDTO updateUser, Integer id) {
        User user = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "does not exist!!!"));

        user.setName(updateUser.getName().trim());
        user.setEmail(updateUser.getEmail().trim());
        user.setRole(updateUser.getRole());

        repository.saveAndFlush(user);
        return updateUser;
    }

}


