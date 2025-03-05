package com.codegym.mapper;

import com.codegym.model.Review;
import com.codegym.model.dto.review.ReviewDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ReviewDTOMapper {
    public ReviewDTO toReviewDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setUserName(review.getBooking().getUser().getFullName());
        reviewDTO.setUserImage(review.getBooking().getUser().getAvatar());
        reviewDTO.setUpdatedAt(review.getUpdatedAt());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setHidden(review.isHidden());
        return reviewDTO;
    }
}
