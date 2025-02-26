package com.codegym.controller;

import com.codegym.model.Notification;
import com.codegym.model.User;
import com.codegym.service.notification.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationController {
    @Autowired
    private INotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;
    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(User host, String message) {
        Notification noti = new Notification();
        noti.setHost(host);
        noti.setMessage(message);
        notificationService.save(noti);
        List<Notification> notifications = notificationService.findByHost(host);
        messagingTemplate.convertAndSend("/topic/notifications/" + host.getUsername(), notifications);
    }
}
