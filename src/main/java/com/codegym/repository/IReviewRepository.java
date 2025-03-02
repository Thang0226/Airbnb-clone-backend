package com.codegym.repository;

import com.codegym.model.Booking;
import com.codegym.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

    Review findReviewByBooking(Booking booking);
}
