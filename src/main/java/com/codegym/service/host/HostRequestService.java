package com.codegym.service.host;

import com.codegym.model.HostRequest;
import com.codegym.repository.IHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HostRequestService implements IHostRequestService {
    @Autowired
    private IHostRepository hostRepository;

    @Override
    public Iterable<HostRequest> findAll() {
        return hostRepository.findAll();
    }

    @Override
    public Optional<HostRequest> findById(Long id) {
        return hostRepository.findById(id);
    }

    @Override
    public void save(HostRequest object) {
        hostRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        hostRepository.deleteById(id);
    }

    @Override
    public Integer[] getIncomeByMonth(String hostUsername, Integer numberOfMonth) {
        return new Integer[0];
    }

    @Override
    public Integer[] getIncomeByQuarter(String hostUsername, Integer numberOfQuarter) {
        return new Integer[0];
    }

    @Override
    public Integer[] getIncomeByYear(String hostUsername, Integer numberOfYear) {
        return new Integer[0];
    }
}
