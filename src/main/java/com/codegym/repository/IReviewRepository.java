package com.codegym.repository;

import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

    Review findReviewByBooking(Booking booking);

    @Query("SELECT r FROM Review r " +
            "JOIN r.booking b " +
            "WHERE b.house.id = :houseId")
    List<Review> findAllByHouseId(@Param("houseId") Long houseId);
}
