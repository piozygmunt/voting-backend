package voting.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import voting.exception.ResourceNotFoundException;
import voting.model.auth.User;
import voting.model.common.ApiResponse;
import voting.model.common.Notification;
import voting.model.vote.*;
import voting.repositories.NotificationRepository;
import voting.repositories.UserRepository;
import voting.repositories.VotingInvitationRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    static private String channelURL = "/queue/notification";
    private SimpMessagingTemplate simpMessagingTemplate;
    private NotificationRepository<Notification> notificationRepository;
    private NotificationRepository<InviteNotification> inviteNotificationRepository;
    private VotingInvitationRepository votingInvitationRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    public NotificationService(SimpMessagingTemplate simpMessagingTemplate, NotificationRepository<Notification> notificationRepository, NotificationRepository<InviteNotification> inviteNotificationRepository, VotingInvitationRepository votingInvitationRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationRepository = notificationRepository;
        this.inviteNotificationRepository = inviteNotificationRepository;
        this.votingInvitationRepository = votingInvitationRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private List<NotificationDTO> getAllUnseenNotificationForUser(User user) {
        List<NotificationDTO> notifications;
        notifications = notificationRepository.findAllByUserAndStateAndAndCreatedAtBefore(user, Notification.State.UNSEEN, Instant.now())
        .stream().map(notification -> modelMapper.map(notification, NotificationDTO.class)).collect(Collectors.toList());
        return notifications;
    }

    private void sendPublicNotification(Notification notification) {
        simpMessagingTemplate.convertAndSend(channelURL, notification);
    }

    private void sendToUser(String username, NotificationDTO notification) {
        simpMessagingTemplate.convertAndSendToUser(username, channelURL, notification);
    }

    private Notification createNotification(Notification.Type type, String message, User user) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setUser(user);
        notification.setCreatedAt(Instant.now());
        notification.setState(Notification.State.UNSEEN);
        notification.setAckRequired(true);
        return notification;
    }

    private InviteNotification createInviteNotification(Notification.Type type, String message, User user) {
        InviteNotification notification = new InviteNotification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setUser(user);
        notification.setCreatedAt(Instant.now());
        notification.setState(Notification.State.UNSEEN);
        notification.setAckRequired(true);
        return notification;
    }

    public void sendNotification(Notification.Type type, String message, User user) {
        Notification notification = createNotification(type, message, user);
        notificationRepository.save(notification);

        if (user != null) sendToUser(user.getUsername(), modelMapper.map(notification, NotificationDTO.class));
        else sendPublicNotification(notification);
    }

    public ApiResponse ackNotification(NotificationSeenRequest notificationSeenRequest) {
        long id = notificationSeenRequest.getNotificationId();
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("notification ", "id", id));
        notification.setSeenAt(Instant.now());
        notification.setState(Notification.State.SEEN);
        notificationRepository.save(notification);
        log.info("Notification {} has been confiremed.", notification);


        return new ApiResponse(true, "Notification has been confirmed.");

    }


    public void sendInviteNotification(Notification.Type type, String message, User user, VotingInvitation votingInvitation) {
        log.info("sending notification");
        InviteNotification inviteNotification = createInviteNotification(type, message, user);
        inviteNotification.setVotingInvitation(votingInvitation);
        votingInvitationRepository.save(votingInvitation);
        inviteNotificationRepository.save(inviteNotification);
        log.info("mapped {} " ,modelMapper.map(inviteNotification, InviteNotificationDTO.class));
        sendToUser(user.getUsername(), modelMapper.map(inviteNotification, InviteNotificationDTO.class));
    }

    public void sendUnseenNotifications(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user ", "username", username));

        getAllUnseenNotificationForUser(user).forEach(notification -> {
            log.info("sending notification {}", notification);
            sendToUser(username, notification);
        });
    }
}
