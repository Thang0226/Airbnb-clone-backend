package com.codegym.repository;

import com.codegym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String userName);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);
  
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    Iterable<User> findAllUserRole(@Param("role")String role);
}
