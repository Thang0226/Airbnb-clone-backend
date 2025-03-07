package com.codegym.model.dto.booking;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingDTOForReview {
    private Long id;
    private String houseName;
    private String customerName;
    private String customerImage;
}
