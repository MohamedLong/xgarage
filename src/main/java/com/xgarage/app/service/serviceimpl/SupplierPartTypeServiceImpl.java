package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.SupplierPartTypeRepository;
import com.xgarage.app.model.PartType;
import com.xgarage.app.service.SupplierPartTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierPartTypeServiceImpl implements SupplierPartTypeService {

    @Autowired
    private SupplierPartTypeRepository supplierPartTypeRepository;


    @Override
    public PartType save(PartType partType) {
        return supplierPartTypeRepository.save(partType);
    }

    @Override
    public PartType getById(Long typeId) {
        return supplierPartTypeRepository.getById(typeId);
    }
}
