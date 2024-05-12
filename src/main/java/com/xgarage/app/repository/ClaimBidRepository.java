package com.xgarage.app.repository;

import com.xgarage.app.model.ClaimBid;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimBidRepository extends GenericRepository<ClaimBid> {

    @Query(value = "select * from claim_bid where bid_id = :bidId", nativeQuery = true)
    List<ClaimBid> findByBid(Long bidId);
}
