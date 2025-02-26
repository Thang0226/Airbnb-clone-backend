package com.codegym.service.notification;

import com.codegym.model.Notification;
import com.codegym.service.IGenerateService;

import java.util.List;

public interface INotificationService extends IGenerateService<Notification> {
    List<Notification> findByHostUsername(String hostUsername);
}
