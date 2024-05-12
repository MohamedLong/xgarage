package com.xgarage.app.service;

import com.xgarage.app.model.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface StatusService {
    Status getStatus(Long statusId);

    Status findStatus(Long statusId);

    Status saveStatus(Status status);

    List<Status> findAll();
}
