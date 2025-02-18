package com.codegym.controller;
import com.codegym.config.jwt.JwtResponse;
import com.codegym.exception.PhoneAlreadyExistsException;
import com.codegym.exception.UsernameAlreadyExistsException;
import com.codegym.model.HostRequest;
import com.codegym.model.auth.AuthenticationRequest;
import com.codegym.model.auth.Account;
import com.codegym.model.auth.Role;
import com.codegym.model.dto.UpdatePasswordDTO;
import com.codegym.model.dto.UserDTO;
import com.codegym.model.dto.UserProfileDTO;
import com.codegym.model.User;
import com.codegym.model.UserForm;
import com.codegym.config.jwt.JwtService;
import com.codegym.service.host.IHostRequestService;
import com.codegym.service.role.IRoleService;
import com.codegym.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private IHostRequestService hostRequestService;

    @Value("${file_upload}")
    private String fileUpload;

    // Admin: fetches all pending host requests
    @GetMapping("/host-requests")
    public ResponseEntity<List<HostRequest>> getHostRequests() {
        List<HostRequest> requests = (List<HostRequest>) hostRequestService.findAll();
        return ResponseEntity.ok(requests);
    }

    // Admin: approve request to be Host
    @PostMapping("/host-requests/{requestId}/approve")
    public ResponseEntity<?> approveHostRequest(@PathVariable Long requestId) {
        try {
            // Find the request
            Optional<HostRequest> request = hostRequestService.findById(requestId);
            if (request.isEmpty()) {
                return new ResponseEntity<>("Request not found", HttpStatus.NOT_FOUND);
            }

            // Get the user from the request
            User user = request.get().getUser();

            // Add host role
            Role hostRole = roleService.findByName("ROLE_HOST");
            Set<Role> roles = user.getRoles();
            roles.add(hostRole);
            user.setRoles(roles);
            userService.save(user);

            // Delete the request
            hostRequestService.deleteById(requestId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        try {
            System.out.println(account.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.findByUsername(account.getUsername()).get();

            return ResponseEntity.ok(new JwtResponse(
                    currentUser.getId(),
                    jwt,
                    userDetails.getUsername(),
                    currentUser.getFullName(),
                    userDetails.getAuthorities()));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName("ROLE_USER");
        roles.add(userRole);
        user.setRoles(roles);
        userService.save(user);

        // If they want to be a host, create a host request
        if (userDTO.isHost()) {
            HostRequest hostRequest = new HostRequest();
            hostRequest.setUser(user);
//            hostRequest.setRequestDate(LocalDateTime.now());
            hostRequest.setStatus("PENDING");
            hostRequestService.save(hostRequest);
        }

//        user.setRoles(roles);
//        System.out.println("User roles before saving: " + roles);
//        userService.save(user);
//        System.out.println("User saved with roles: " + user.getRoles());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/register/validate-username")
    public ResponseEntity<?> validateUsername(@RequestBody String username) {
        try {
            userService.validateUsername(username);
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register/validate-phone")
    public ResponseEntity<?> validatePhone(@RequestBody String phone) {
        try {
            userService.validatePhone(phone);
        } catch (PhoneAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtService.extractTokenFromRequest(request);
        if (token != null && jwtService.validateJwtToken(token)) {
            // [IF REQUIRED] Add token to a blacklist or perform other logout logic
            return ResponseEntity.ok("Logged out successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }

    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        Optional<User> userOptional = userService.findByUsername(updatePasswordDTO.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
                String newPassword = passwordEncoder.encode(updatePasswordDTO.getNewPassword());
                user.setPassword(newPassword);
                userService.save(user);
                return ResponseEntity.ok("Password changed successfully!");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password.");
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
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
        UserProfileDTO userProfile = userService.getUserProfile(userName);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@Valid @ModelAttribute UserForm userForm, BindingResult result) {
        Optional<User> userOptional = userService.findByUsername(userForm.getUsername());
        return getResponseEntity(userForm, userOptional, result);
    }

    private ResponseEntity<?> getResponseEntity(UserForm userForm, Optional<User> userOptional, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

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
        return new ResponseEntity<>("User profile update successfully", HttpStatus.OK);
    }
}
