package com.codegym.mapper;

import com.codegym.exception.HouseNotFoundException;
import com.codegym.model.House;
import com.codegym.model.HouseMaintenance;
import com.codegym.model.dto.house.HouseMaintenanceRecordDTO;

import com.codegym.service.house.IHouseService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HouseMaintenanceMapper {

    @Mapping(target = "house", expression = "java(getHouseById(dto.getHouseId(), houseService))")
    HouseMaintenance toHouseMaintenance(HouseMaintenanceRecordDTO dto, @Context IHouseService houseService);

    @Mapping(target = "houseId", source = "house.id")
    @Mapping(target = "userId", source = "house.host.id")
    HouseMaintenanceRecordDTO toHouseMaintenanceRecordDTO(HouseMaintenance houseMaintenance);

    default House getHouseById(Long houseId, IHouseService houseService) {
        return houseService.findById(houseId).orElseThrow(() ->
                new HouseNotFoundException("House not found with id: " + houseId)
        );
    }
}
