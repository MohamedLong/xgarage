package com.xgarage.app.service;

import com.xgarage.app.model.PartType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface SupplierPartTypeService {
    PartType save(PartType partType);

    PartType getById(Long typeId);
}
