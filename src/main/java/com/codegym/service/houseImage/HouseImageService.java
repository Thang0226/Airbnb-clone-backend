package com.codegym.service.houseImage;

import com.codegym.model.HouseImage;
import com.codegym.repository.IHouseImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseImageService implements IHouseImageService {
    @Autowired
    private IHouseImageRepository houseImageRepository;

    @Override
    public Iterable<HouseImage> findAll() {
        return houseImageRepository.findAll();
    }

    @Override
    public Optional<HouseImage> findById(Long id) {
        return houseImageRepository.findById(id);
    }

    @Override
    public void save(HouseImage object) {
        houseImageRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        houseImageRepository.deleteById(id);
    }

    @Override
    public List<HouseImage> findAllByHouse_Id(Long houseId) {
        return houseImageRepository.findAllByHouse_Id(houseId);
    }
}
