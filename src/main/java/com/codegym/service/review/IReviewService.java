package com.codegym.service.review;

import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.Review;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewService extends IGenerateService<Review> {

    Review findReviewByBooking(Booking booking);

    List<Review> findAllByHouseId(Long houseId);
}
