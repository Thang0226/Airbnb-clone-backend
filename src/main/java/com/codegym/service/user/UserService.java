package com.codegym.service.user;
import com.codegym.exception.NoSuchUserExistsException;
import com.codegym.exception.PhoneAlreadyExistsException;
import com.codegym.exception.UsernameAlreadyExistsException;
import com.codegym.model.auth.UserPrincipal;
import com.codegym.model.dto.UserProfileDTO;
import com.codegym.model.User;
import com.codegym.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public Optional<UserProfileDTO> getUserProfile(String userName) {
        return userRepository.getUserProfileByUsername(userName);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchUserExistsException("NO USER PRESENT WITH ID = " + id);
        }
        return user;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void validateUsername(String username) {
        Optional<User> user_username = userRepository.findByUsername(username);
        if (user_username.isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
    }

    @Override
    public void validatePhone(String phone) {
        Optional<User> user_phone = userRepository.findByPhone(phone);
        if (user_phone.isPresent()) {
            throw new PhoneAlreadyExistsException("Phone number already exists");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return UserPrincipal.build(userOptional.get());
        } else {
            throw new UsernameNotFoundException("NO USER PRESENT WITH USERNAME = " + username);
        }
    }
}
