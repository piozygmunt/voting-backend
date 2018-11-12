package voting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import voting.model.auth.UserDTO;
import voting.model.common.ApiResponse;
import voting.service.UserService;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public List<UserDTO> getUser(@RequestParam(value="q", required = false, defaultValue = "") String searchUsername) {
        log.info("q= {}", searchUsername);
        return userService.getUsersUsernameStartingWith(searchUsername);

    }

    @GetMapping("/api/user/checkUsername")
    public ApiResponse checkUsername(@RequestParam(value="q") String username) {
        log.info("q= {}", username);
        return userService.checkUsername(username);

    }

    @GetMapping("/api/user/checkEmail")
    public ApiResponse checkEmail(@RequestParam(value="q") String email) {
        log.info("q= {}", email);
        return userService.checkEmail(email);

    }
}
