package com.codegym.controller;

import com.codegym.model.User;
import com.codegym.model.UserForm;
import com.codegym.model.dto.UserProfileDTO;
import com.codegym.service.user.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin ("*")
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @Value("${file_upload}")
    private String fileUpload;

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        user.setAvatar("default.jpg");
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @ModelAttribute UserForm userForm, BindingResult result) {
        Optional<User> userOptional = userService.findById(id);
        return getResponseEntity(userForm, userOptional, result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/profile/{userName}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String userName) {
        Optional<UserProfileDTO> userProfile = userService.getUserProfile(userName);
        return userProfile
                .map(userProfileDTO -> new ResponseEntity<>(userProfileDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@Valid @ModelAttribute UserForm userForm, BindingResult result) {
        Optional<User> userOptional = userService.findByUsername(userForm.getUsername());
        return getResponseEntity(userForm, userOptional, result);
    }

    private ResponseEntity<?> getResponseEntity(UserForm userForm, Optional<User> userOptional, BindingResult result) {
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found!",HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();

        MultipartFile multipartFile = userForm.getAvatar();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = multipartFile.getOriginalFilename();
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(fileUpload + fileName));
                user.setAvatar(fileName);
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        user.setFullName(userForm.getFullName());
        user.setAddress(userForm.getAddress());
        user.setPhone(userForm.getPhone());
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
