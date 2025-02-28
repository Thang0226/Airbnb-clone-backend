package com.codegym.service.house;

import com.codegym.model.HouseMaintenance;
import com.codegym.model.dto.house.HouseMaintenanceRecordDTO;
import com.codegym.service.IGenerateService;

import java.util.List;

public interface IHouseMaintenanceService extends IGenerateService<HouseMaintenance> {
    List<HouseMaintenanceRecordDTO> findByHouseId(Long houseId);
}
