package com.codegym.service.review;

import com.codegym.model.Booking;
import com.codegym.model.Review;
import com.codegym.service.IGenerateService;

public interface IReviewService extends IGenerateService<Review> {

    Review findReviewByBooking(Booking booking);
}
