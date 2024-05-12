package com.xgarage.app.service;

import com.xgarage.app.model.Principle;
import com.xgarage.app.repository.PrincipleRepository;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrincipleService extends GenericService<Principle> {

    @Autowired private PrincipleRepository principleRepository;
    public PrincipleService(GenericRepository<Principle> repository) {
        super(repository);
    }

    public List<Principle> findByTenent(Long tenantId) {
        return principleRepository.findByTenant(tenantId);
    }
}
