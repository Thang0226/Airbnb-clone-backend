package com.codegym.repository;

import com.codegym.model.HostRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHostRequestRepository extends JpaRepository<HostRequest, Long> {
}
