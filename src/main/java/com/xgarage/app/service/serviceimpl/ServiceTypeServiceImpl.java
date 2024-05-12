package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.ServiceTypeRepository;
import com.xgarage.app.model.ServiceType;
import com.xgarage.app.service.ServiceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ServiceTypeServiceImpl implements ServiceTypeService {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Override
    public ServiceType save(ServiceType serviceType) {
        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public ServiceType findServiceTypeById(Long serviceId) {
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(serviceId);
        return serviceTypeOptional.orElse(null);
    }
    @Override
    public List<ServiceType> getAllServiceTypes() {
        return serviceTypeRepository.findAll();
    }
}
