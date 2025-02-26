package com.codegym.repository;

import com.codegym.model.User;
import com.codegym.model.dto.host.HostInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String userName);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_USER'")
    Page<User> findAllUsers(Pageable pageable);

    @Query(nativeQuery = true, value = "call get_all_hosts_info()")
    List<HostInfoDTO> getAllHostsInfo();

    @Query(nativeQuery = true, value = "call get_host_info(:hostId)")
    HostInfoDTO getHostInfo(@Param("hostId") Long hostId);
}
