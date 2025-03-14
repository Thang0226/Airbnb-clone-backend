package com.codegym.service.user;

import com.codegym.exception.NoSuchUserExistsException;
import com.codegym.exception.PhoneAlreadyExistsException;
import com.codegym.exception.UsernameAlreadyExistsException;
import com.codegym.mapper.UserMapper;
import com.codegym.model.auth.UserPrincipal;
import com.codegym.model.constants.UserStatus;
import com.codegym.model.dto.host.HostInfoDTO;
import com.codegym.model.dto.user.UserInfoDTO;
import com.codegym.model.User;
import com.codegym.model.dto.user.UserProfileDTO;
import com.codegym.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserProfileDTO getUserProfile(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new NoSuchUserExistsException(userName));
        return userMapper.toUserProfileDTO(user);
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

    @Override
    public Page<UserInfoDTO> getAllUsersInfo(Pageable pageable) {
        Page<User> users = userRepository.findAllUsers(pageable);
        return users.map(userMapper::toUserInfoDTO);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean validateEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.isEmpty();
    }

    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserExistsException("NO USER PRESENT WITH ID = " + userId));
        return userMapper.toUserInfoDTO(user);
    }

    @Override
    public Page<HostInfoDTO> getAllHostsInfo(Pageable pageable) {
        List<HostInfoDTO> hosts = userRepository.getAllHostsInfo();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), hosts.size());
        List<HostInfoDTO> pagedList = hosts.subList(start, end);
        return new PageImpl<>(pagedList, pageable, hosts.size());
    }

    @Override
    public HostInfoDTO getHostInfo(Long userId) {
        return userRepository.getHostInfo(userId);
    }

    @Override
    public List<Long> getIncomeByMonth(String hostUsername, Integer numberOfMonth) {
        List<Long> incomeList = null;
        try {
            incomeList = userRepository.getHostIncomeByMonth(hostUsername, numberOfMonth);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return incomeList;
    }

    @Override
    public List<Long> getIncomeByYear(String hostUsername, Integer numberOfYear) {
        List<Long> incomeList = null;
        try {
            incomeList = userRepository.getHostIncomeByYear(hostUsername, numberOfYear);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return incomeList;
    }

    @Override
    public User updateUserStatus(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.LOCKED : UserStatus.ACTIVE);
            return userRepository.save(user);
        }).orElseThrow(() -> new NoSuchUserExistsException("No user with id " + id + " found"));
    }
}
