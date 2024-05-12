package com.xgarage.app.service;

import com.xgarage.app.model.PartType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface PartTypeService {
    List<PartType> getAllPartTypes();

    PartType findPartTypeById(Long partTypeId);

    List<PartType> getAllPartTypesByRequestId(Long id);

    PartType savePartType(PartType partType);
}
