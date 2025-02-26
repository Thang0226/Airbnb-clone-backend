package com.codegym.controller;

import com.codegym.service.notification.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    @Autowired
    private INotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;
    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String hostUsername, String message) {
        messagingTemplate.convertAndSend("/topic/notifications/" + hostUsername, message);
    }
}
