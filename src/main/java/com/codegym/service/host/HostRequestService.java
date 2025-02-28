package com.codegym.service.host;

import com.codegym.model.HostRequest;
import com.codegym.repository.IHostRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HostRequestService implements IHostRequestService {
    @Autowired
    private IHostRequestRepository hostRequestRepository;

    @Override
    public Iterable<HostRequest> findAll() {
        return hostRequestRepository.findAll();
    }

    @Override
    public Optional<HostRequest> findById(Long id) {
        return hostRequestRepository.findById(id);
    }

    @Override
    public void save(HostRequest object) {
        hostRequestRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        hostRequestRepository.deleteById(id);
    }
}
