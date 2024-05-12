package com.xgarage.app.service.serviceimpl;
import com.xgarage.app.repository.PartTypeRepository;
import com.xgarage.app.model.PartType;
import com.xgarage.app.service.PartTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class PartTypeServiceImpl implements PartTypeService {

    @Autowired
    private PartTypeRepository partTypeRepository;

    @Override
    public List<PartType> getAllPartTypes() {
        return partTypeRepository.findAll();
    }

    @Override
    public PartType findPartTypeById(Long partTypeId) {
        Optional<PartType> partTypeOptional = partTypeRepository.findById(partTypeId);
        return partTypeOptional.orElse(null);
    }

    @Override
    public List<PartType> getAllPartTypesByRequestId(Long id){
        return partTypeRepository.findAllPartTypesByRequestId(id);
    }

    @Override
    public PartType savePartType(PartType partType){
        return partTypeRepository.save(partType);
    }

}
