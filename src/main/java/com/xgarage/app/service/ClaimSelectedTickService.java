package com.xgarage.app.service;

import com.xgarage.app.model.ClaimSelectedTick;
import com.xgarage.app.model.ClaimTickId;
import com.xgarage.app.repository.ClaimSelectedTickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClaimSelectedTickService {

    @Autowired
    private ClaimSelectedTickRepository claimSelectedTickRepository;

    public ClaimSelectedTick save(ClaimSelectedTick selectedTick) {
        ClaimSelectedTick tick = new ClaimSelectedTick(selectedTick.getClaim(), selectedTick.getTick(), selectedTick.getRemarks());
        ClaimSelectedTick savedTick = claimSelectedTickRepository.save(tick);
        return savedTick;
    }

    public ClaimSelectedTick getById(ClaimTickId claimTicksId) {
        return claimSelectedTickRepository.getReferenceById(claimTicksId);
    }
}
