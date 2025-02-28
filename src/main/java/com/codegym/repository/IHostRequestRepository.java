package com.codegym.repository;

import com.codegym.model.HostRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IHostRequestRepository extends JpaRepository<HostRequest, Long> {

    @Query("select reqs from HostRequest reqs order by reqs.requestDate desc")
    Iterable<HostRequest> findAllOrderByRequestDateDesc();
}
