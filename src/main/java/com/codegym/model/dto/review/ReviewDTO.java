package com.codegym.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String userImage;
    private String userName;
    private LocalDate updatedAt;
    private Integer rating;
    private String comment;
    private boolean isHidden;
}
