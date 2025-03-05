package com.codegym.service.house;

import com.codegym.exception.AvailabilityNotFoundException;
import com.codegym.exception.booking.OverlappingBookingException;
import com.codegym.exception.house_maintenance.InvalidMaintenanceDateException;
import com.codegym.exception.house_maintenance.OverlappingMaintenanceException;
import com.codegym.mapper.HouseMaintenanceMapper;
import com.codegym.model.Availability;
import com.codegym.model.HouseMaintenance;
import com.codegym.model.dto.house.HouseMaintenanceRecordDTO;
import com.codegym.repository.IHouseMaintenanceRepository;
import com.codegym.service.availability.IAvailabilityService;
import com.codegym.service.booking.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private IBookingService bookingService;

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
    @Transactional
    public void save(HouseMaintenance houseMaintenance) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = houseMaintenance.getStartDate();
        LocalDate endDate = houseMaintenance.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new InvalidMaintenanceDateException("End date must be after start date.");
        }

        if (endDate.isBefore(today) || (today.isAfter(startDate) && today.isBefore(endDate))) {
            throw new InvalidMaintenanceDateException("Invalid maintenance period. Please check the dates.");
        }

        Long houseId = houseMaintenance.getHouse().getId();

        // Kiểm tra trùng lịch bảo trì
        boolean isOverlapMaintenance = houseMaintenanceRepository.overlappingMaintenance(houseId, startDate, endDate);
        if (isOverlapMaintenance) {
            throw new OverlappingMaintenanceException(
                    "The house is already scheduled for maintenance during the selected period. " +
                            "Please choose a different date range."
            );
        }

        // Kiểm tra trùng lịch đặt phòng
        boolean isOverlapBooking = bookingService.overlappingBooking(houseId, startDate, endDate);
        if (isOverlapBooking) {
            throw new OverlappingBookingException(
                    "This house has a booking during the selected maintenance period. " +
                            "Please choose a different date."
            );
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
