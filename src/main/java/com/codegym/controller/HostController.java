package com.codegym.controller;

import com.codegym.model.Notification;
import com.codegym.model.User;
import com.codegym.service.notification.INotificationService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/hosts")
public class HostController {
    @Autowired
    private INotificationService notificationService;
    @Autowired
    private IUserService userService;

    @GetMapping("/{username}/notifications")
    public ResponseEntity<?> getHostNotifications(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }
        User host = userOptional.get();
        List<Notification> notifications = notificationService.findByHost(host);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/{username}/income-stats")
    public ResponseEntity<?> getHostIncomeStats(@PathVariable String username, @RequestParam(defaultValue = "month") String period) {
        final Integer numberOfMonths = 12;
        final Integer numberOfYears = 5;

        if (period == null || period.equals("month")) {
            return new ResponseEntity<>(userService.getIncomeByMonth(username, numberOfMonths), HttpStatus.OK);
        } else if (period.equals("year")) {
            return new ResponseEntity<>(userService.getIncomeByYear(username, numberOfYears), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid period", HttpStatus.BAD_REQUEST);
        }
    }
}
