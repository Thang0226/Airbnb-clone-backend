package com.codegym.service.booking;

import com.codegym.model.Booking;
import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.dto.UserRentalHistoryDTO;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface IBookingService extends IGenerateService<Booking> {
    Page<Booking> getBookingsByUserId(Long userId, Pageable pageable);

    Page<UserRentalHistoryDTO> getUserRentalHistory(Long userID, Pageable pageable);

    BigDecimal getTotalRentPaidByUserId(Long userId);
}
