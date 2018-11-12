package voting.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import voting.exception.AppException;
import voting.model.auth.Role;
import voting.model.auth.SignUpRequest;
import voting.model.auth.User;
import voting.model.auth.UserDTO;
import voting.model.common.ApiResponse;
import voting.repositories.RoleRepository;
import voting.repositories.UserRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public List<UserDTO> getUsersUsernameStartingWith(String arg) {
        List<User> allByUsernameStartingWith = userRepository.findAllByUsernameStartingWith(arg);

        log.info("arg= {}", arg);
        log.info("list= {}", allByUsernameStartingWith);

        Type listTYpe = new TypeToken<List<UserDTO>>() {}.getType();

        return modelMapper.map(allByUsernameStartingWith, listTYpe);
    }

    public User createNewUser(SignUpRequest signUpRequest) {

        Role userRole = roleRepository.findDistinctFirstByName("ROLE_USER").orElseThrow(() -> new AppException("Specified role was not found"));

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());

        List<Role> roles = new ArrayList<>();
        roles.add(userRole);

        user.setRoles(roles);
        user.setPassword("{noop}" + signUpRequest.getPassword());
        user.setEnabled(true);

        return userRepository.save(user);

    }

    public ApiResponse checkUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            return new ApiResponse(false, "Username already taken.");
        }
        else {
            return new ApiResponse(true, "Username available.");
        }
    }


    public ApiResponse checkEmail(String email) {
        Optional<User> userOptional = userRepository.findByUsername(email);
        if(userOptional.isPresent()){
            return new ApiResponse(false, "Email already taken.");
        }
        else {
            return new ApiResponse(true, "Email available.");
        }
    }
}
