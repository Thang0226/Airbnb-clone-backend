package com.codegym.repository;

import com.codegym.model.dto.UserProfileDTO;
import com.codegym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true, value = "call get_user_profile_by_username(:userName)")
    Optional<UserProfileDTO> getUserProfileByUsername(@Param("userName") String userName);

    Optional<User> findByUsername(String userName);
}
