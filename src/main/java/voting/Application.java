package voting;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import voting.model.auth.Role;
import voting.model.auth.User;
import voting.model.chat.Message;
import voting.model.common.Notification;
import voting.model.vote.*;
import voting.repositories.*;
import voting.service.VotingService;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private PickItemRepository pickItemRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Notification.class, NotificationDTO.class);
        modelMapper.createTypeMap(InviteNotification.class, InviteNotificationDTO.class).includeBase(Notification.class, NotificationDTO.class);
        return new ModelMapper();
    }



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        // for testing purpose
        List<PickingItem> possibilities = new ArrayList<>();


        for(int i =0; i < 10; ++i) {
            PickingItem item = new PickingItem();
            item.setImgUrl("http://localhost:8080/img/inferno.jpeg");
            item.setName("item"+i);
            pickItemRepository.save(item);
            possibilities.add(item);
        }




        Role role = new Role();
        role.setName("ROLE_USER");


        roleRepository.save(role);



    }
}
