package com.xgarage.app.repository;

import com.xgarage.app.model.ClaimParts;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimPartsRepository extends GenericRepository<ClaimParts> {
    @Query(value = "select * from claim_parts where claim_id = :claimId", nativeQuery = true)
    List<ClaimParts> findByClaim(Long claimId);
}
