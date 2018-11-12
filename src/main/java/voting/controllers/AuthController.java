package voting.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import voting.config.JwtTokenProvider;
import voting.model.auth.*;
import voting.model.common.ApiResponse;
import voting.repositories.RoleRepository;
import voting.service.NotificationService;
import voting.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final
    AuthenticationManager authenticationManager;

    private final
    UserService userService;

    private final
    RoleRepository roleRepository;

    private final
    JwtTokenProvider tokenProvider;

    private final
    NotificationService notificationService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, RoleRepository roleRepository, JwtTokenProvider tokenProvider, NotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.tokenProvider = tokenProvider;
        this.notificationService = notificationService;
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        return new JwtAuthenticationResponse(jwt);
    }

    @GetMapping("/profile/notifications")
    public ApiResponse notifications(@AuthenticationPrincipal User user) {
        notificationService.sendUnseenNotifications(user.getUsername());
        return new ApiResponse(true, "Notifications fetched succesfully");
    }


    @GetMapping("/profile")
    public UserDTO authenticateUser(@AuthenticationPrincipal User user) {
        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        userService.createNewUser(signUpRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "User registered successfully"));
    }
}