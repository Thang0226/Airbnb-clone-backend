package com.codegym.controller;

import com.codegym.model.User;
import com.codegym.model.constants.UserStatus;
import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.dto.UserRentalHistoryDTO;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

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

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value ->
                        new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("No user with id " + id + "founded",HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        String userStatus = user.getStatus().toString();
        if (userStatus.equals("ACTIVE")) {
            user.setStatus(UserStatus.LOCKED);
            userService.save(user);
            return new ResponseEntity<>("User locked", HttpStatus.OK);
        } else {
            user.setStatus(UserStatus.ACTIVE);
            userService.save(user);
            return new ResponseEntity<>("User unlocked", HttpStatus.OK);
        }
    }

    @GetMapping("/user-details/{id}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long id) {
        UserInfoDTO userInfo = userService.getUserInfo(id);
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
}
