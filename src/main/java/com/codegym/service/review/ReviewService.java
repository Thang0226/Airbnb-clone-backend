package com.codegym.service.review;

import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.Review;
import com.codegym.repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService implements IReviewService {
    @Autowired
    private IReviewRepository reviewRepository;

    @Override
    public Iterable<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public void save(Review review) {
        reviewRepository.save(review);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public Review findReviewByBooking(Booking booking) {
        return reviewRepository.findReviewByBooking(booking);
    }

    @Override
    public List<Review> findAllByHouseId(Long houseId) {
        return reviewRepository.findAllByHouseId(houseId);
    }
}
