package com.codegym.repository;

import com.codegym.model.Notification;
import com.codegym.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findAllByHostOrderByCreatedAtDesc(User host);
}
