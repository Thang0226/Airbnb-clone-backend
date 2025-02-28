package com.codegym.model.dto.house;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class HouseMaintenanceRecordDTO {
    private Long houseId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
