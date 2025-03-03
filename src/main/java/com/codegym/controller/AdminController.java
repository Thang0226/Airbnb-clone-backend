package com.codegym.controller;

import com.codegym.exception.NoSuchUserExistsException;
import com.codegym.model.User;
import com.codegym.model.constants.UserStatus;
import com.codegym.model.dto.host.HostInfoDTO;
import com.codegym.model.dto.user.UserInfoDTO;
import com.codegym.model.dto.user.UserRentalHistoryDTO;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoDTO>> getAllUsers(Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUsersInfo(pageable), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id) {
        try {
            User user = userService.updateUserStatus(id);
            String message = "User " + (user.getStatus() == UserStatus.ACTIVE ? "unlocked" : "locked");
            return ResponseEntity.ok(message);
        } catch (NoSuchUserExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        UserInfoDTO userInfo = userService.getUserInfo(id);
        if (userInfo == null) {
            return new ResponseEntity<>("User not founded", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/user-rental-history/{id}")
    public ResponseEntity<Page<UserRentalHistoryDTO>> getUserRentalHistory(@PathVariable Long id, Pageable pageable) {
        Page<UserRentalHistoryDTO> userRentalHistory = bookingService.getUserRentalHistory(id, pageable);

        return new ResponseEntity<>(userRentalHistory, HttpStatus.OK);
    }

    @GetMapping("/user-payment/{id}")
    public ResponseEntity<?> getUserPayment(@PathVariable Long id) {
        BigDecimal userTotalRentPaid = bookingService.getTotalRentPaidByUserId(id);
        return new ResponseEntity<>(userTotalRentPaid, HttpStatus.OK);
    }

    @GetMapping("/hosts")
    public ResponseEntity<Page<HostInfoDTO>> getAllHosts(Pageable pageable) {
        return new ResponseEntity<>(userService.getAllHostsInfo(pageable), HttpStatus.OK);
    }

    @GetMapping("/hosts/{id}")
    public ResponseEntity<?> getHostInfo(@PathVariable Long id) {
        HostInfoDTO hostInfo = userService.getHostInfo(id);
        if (hostInfo == null) {
            return new ResponseEntity<>("User with id " + id + " is not a host", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(hostInfo, HttpStatus.OK);
    }
}
