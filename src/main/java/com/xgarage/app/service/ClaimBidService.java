package com.xgarage.app.service;

import com.xgarage.app.model.ClaimBid;
import com.xgarage.app.repository.ClaimBidRepository;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClaimBidService extends GenericService<ClaimBid> {

    @Autowired private ClaimBidRepository claimBidRepository;


    public ClaimBidService(GenericRepository<ClaimBid> repository) {
        super(repository);
    }

    public void saveAll(List<ClaimBid> claimBidList) {
        claimBidRepository.saveAll(claimBidList);
    }


    public List<ClaimBid> findByBid(Long bidId) {
        return claimBidRepository.findByBid(bidId);
    }
}
