package voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import voting.model.common.ApiResponse;
import voting.model.vote.NotificationSeenRequest;
import voting.service.NotificationService;

@RestController
@CrossOrigin
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/api/notification/ack")
    public ApiResponse acceptNotification(@RequestBody NotificationSeenRequest notificationSeenRequest) {
        return notificationService.ackNotification(notificationSeenRequest);
    }


}
