package voting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import voting.model.auth.User;
import voting.repositories.UserRepository;

import javax.transaction.Transactional;

@Configuration
@Slf4j
public class CustomUserServiceDetails implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserServiceDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("username " + s + " not found"));

    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
    }
}
