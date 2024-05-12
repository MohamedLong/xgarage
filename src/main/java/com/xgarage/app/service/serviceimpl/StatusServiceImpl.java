package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.Status;
import com.xgarage.app.repository.StatusRepository;
import com.xgarage.app.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Status getStatus(Long statusId) {
        return statusRepository.getReferenceById(statusId);
    }

    @Override
    public Status findStatus(Long statusId) {
        Optional<Status> optionalStatus = statusRepository.findById(statusId);
        return optionalStatus.orElse(null);
    }


    @Override
    public Status saveStatus(Status status) {
        return statusRepository.save(status);
    }

    @Override
    public List<Status> findAll() {
        return statusRepository.findAll();
    }
}
