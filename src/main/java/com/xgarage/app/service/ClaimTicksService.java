package com.xgarage.app.service;

import com.xgarage.app.model.ClaimTicks;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClaimTicksService extends GenericService<ClaimTicks> {

    public ClaimTicksService(GenericRepository<ClaimTicks> repository) {
        super(repository);
    }
}
