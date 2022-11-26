package sit.int221.oasip.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sit.int221.oasip.DTO.LoginDTO;
import sit.int221.oasip.Service.MatchService;
import sit.int221.oasip.Service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    private MatchService service;

    @PostMapping("")
    public boolean match(@RequestBody LoginDTO loginInfo) {
        return service.match(loginInfo.getEmail(), loginInfo.getPassword());
    }
}
