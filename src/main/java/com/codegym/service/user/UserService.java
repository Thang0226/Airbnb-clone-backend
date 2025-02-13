package com.codegym.service.user;

import com.codegym.exception.NoSuchUserExistsException;
import com.codegym.exception.PhoneAlreadyExistsException;
import com.codegym.exception.UsernameAlreadyExistsException;
import com.codegym.model.DTO.UserProfileDTO;
import com.codegym.model.User;
import com.codegym.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
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
    public Optional<User> findById(Long id) throws NoSuchUserExistsException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchUserExistsException("NO USER PRESENT WITH ID = " + id);
        }
        return user;
    }

    @Override
    public void save(User user) throws UsernameAlreadyExistsException, PhoneAlreadyExistsException {
        Optional<User> user_username = userRepository.findByUsername(user.getUsername());
        if (user_username.isPresent()) {
            throw new UsernameAlreadyExistsException("USERNAME ALREADY EXISTS");
        }
        Optional<User> user_phone = userRepository.findByPhone(user.getPhone());
        if (user_phone.isPresent()) {
            throw new PhoneAlreadyExistsException("PHONE NUMBER ALREADY EXISTS");
        }
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
}
