package com.codegym.service.house;

import com.codegym.exception.AvailabilityNotFoundException;
import com.codegym.exception.house_maintenance.DuplicateMaintenanceException;
import com.codegym.exception.house_maintenance.InvalidMaintenanceDateException;
import com.codegym.mapper.HouseMaintenanceMapper;
import com.codegym.model.Availability;
import com.codegym.model.HouseMaintenance;
import com.codegym.model.dto.house.HouseMaintenanceRecordDTO;
import com.codegym.repository.IHouseMaintenanceRepository;
import com.codegym.service.availability.IAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HouseMaintenanceService implements IHouseMaintenanceService {
    @Autowired
    private IHouseMaintenanceRepository houseMaintenanceRepository;

    @Autowired
    private IAvailabilityService availabilityService;

    @Autowired
    private HouseMaintenanceMapper houseMaintenanceMapper;

    @Override
    public Iterable<HouseMaintenance> findAll() {
        return houseMaintenanceRepository.findAll();
    }

    @Override
    public Optional<HouseMaintenance> findById(Long id) {
        return houseMaintenanceRepository.findById(id);
    }

    @Override
    public void save(HouseMaintenance houseMaintenance) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = houseMaintenance.getStartDate();
        LocalDate endDate = houseMaintenance.getEndDate();

        if (endDate.isBefore(today) || (today.isAfter(startDate) && today.isBefore(endDate))) {
            throw new InvalidMaintenanceDateException("Invalid maintenance period. Please check the dates.");
        }

        boolean isExists = houseMaintenanceRepository.existsByHouseIdAndStartDateAndEndDate(
                houseMaintenance.getHouse().getId(), startDate, endDate
        );
        if (isExists) {
            throw new DuplicateMaintenanceException("House maintenance record already exists for this date range.");
        }

        houseMaintenanceRepository.save(houseMaintenance);

        Availability availability = availabilityService.getAvailabilityCoversHouseMaintenance(houseMaintenance);
        if (availability == null) {
            throw new AvailabilityNotFoundException("Can't find availability");
        }
        LocalDate maintenanceStartDate = houseMaintenance.getStartDate();
        LocalDate maintenanceEndDate = houseMaintenance.getEndDate();

        availabilityService.createTwoNewFromAnOldOne(
                maintenanceStartDate, maintenanceEndDate, availability);
    }

    @Override
    public void deleteById(Long id) {
        houseMaintenanceRepository.deleteById(id);
    }

    @Override
    public List<HouseMaintenanceRecordDTO> findByHouseId(Long houseId) {
        List<HouseMaintenance> houseMaintenance = houseMaintenanceRepository.findByHouseId(houseId);
        return houseMaintenance.stream()
                .map(houseMaintenanceMapper::toHouseMaintenanceRecordDTO)
                .collect(Collectors.toList());
    }
}
