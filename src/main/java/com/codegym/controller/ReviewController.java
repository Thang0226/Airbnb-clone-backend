package com.codegym.controller;

import com.codegym.model.Review;
import com.codegym.service.review.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private IReviewService reviewService;

    @PatchMapping("/{id}/hide")
    ResponseEntity<?> hideReview(@PathVariable Long id) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isEmpty()) {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        }
        Review review = reviewOptional.get();
        review.setHidden(true);
        reviewService.save(review);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}
