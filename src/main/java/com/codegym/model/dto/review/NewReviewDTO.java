package com.codegym.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewReviewDTO {
    private Integer rating;
    private String comment;
}
