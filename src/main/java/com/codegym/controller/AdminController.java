package com.codegym.controller;

import com.codegym.model.User;
import com.codegym.model.dto.UserInfoDTO;
import com.codegym.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;

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

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @ModelAttribute UserForm userForm, BindingResult result) {
//        Optional<User> userOptional = userService.findById(id);
//        return getResponseEntity(userForm, userOptional, result);
//    }


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
            user.setStatus(User.Status.valueOf("LOCKED"));
            userService.save(user);
            return new ResponseEntity<>("User locked", HttpStatus.OK);
        } else {
            user.setStatus(User.Status.valueOf("ACTIVE"));
            userService.save(user);
            return new ResponseEntity<>("User unlocked", HttpStatus.OK);
        }
    }
}
