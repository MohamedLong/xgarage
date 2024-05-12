package com.xgarage.app.service;

import com.xgarage.app.model.ServiceType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ServiceTypeService {
    ServiceType save(ServiceType serviceType);

    ServiceType findServiceTypeById(Long serviceId);

    List<ServiceType> getAllServiceTypes();
}
