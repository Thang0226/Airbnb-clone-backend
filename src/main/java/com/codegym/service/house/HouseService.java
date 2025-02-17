package com.codegym.service.house;

import com.codegym.model.House;
import com.codegym.repository.IHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseService implements IHouseService {
    @Autowired
    private IHouseRepository houseRepository;
    @Override
    public Iterable<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public void save(House object) {
        houseRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        houseRepository.deleteById(id);
    }
}
