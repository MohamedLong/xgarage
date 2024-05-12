package com.xgarage.app.repository;

import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.model.Claim;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends GenericRepository<Claim> {

    @Query(value = "select id, claim_no as claimNo, claim_date as claimDate, tenant_id as tenantId, claim_title as claimTitle, (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, claim_parts r where p.id = r.part_id and r.claim_id = claim.id) as partNames, (select name from tenant where id = :tenantId) as tenantName, (select first_name from users where id = claim.created_by) as createdUser, (select name_en from status where id = claim.status_id) as status, updated_at as statusDate from claim where tenant_id = :tenantId", nativeQuery = true)
    List<ClaimVO> findByTenant(Long tenantId, Pageable pageable);

    @Query(value = "select id, claim_no as claimNo, claim_date as claimDate, tenant_id as tenantId, claim_title as claimTitle, (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, claim_parts r where p.id = r.part_id and r.claim_id = claim.id) as partNames, (select name from tenant where id = claim.tenant_id) as tenantName, (select first_name from users where id = claim.created_by) as createdUser, (select name_en from status where id = claim.status_id) as status, updated_at as statusDate from claim", nativeQuery = true)
    List<ClaimVO> findAllClaims(Pageable pageable);

    boolean existsByClaimNo(String claimNo);

    Optional<Claim> findByClaimNo(String claimNo);

    @Query(value = "select id, claim_no as claimNo, claim_date as claimDate, tenant_id as tenantId, (select name from tenant where id = claim.tenant_id) as tenantName, (select first_name from users where id = claim.created_by) as createdUser, claim_title as claimTitle, (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, claim_parts r where p.id = r.part_id and r.claim_id = claim.id) as partNames, (select name_en from status where id = claim.status_id) as status, updated_at as statusDate from claim where (assign_type = 'Direct' and garage_id = :tenant) or " +
            "1 = (case when claim.privacy='Public' then 1 when claim.privacy = 'Private' and claim.id in (select rs.claim_id from claim_garages rs where rs.garage_id = :tenant) then 1 end) and status_id <> (select id from status where name_en = 'Open')", nativeQuery = true)
    List<ClaimVO> findClaimsForRelatedToSupplier(Long tenant, Pageable page);

    @Query(value = "select distinct claim.id as id, bid.id as bidId, claim_no as claimNo, claim_date as claimDate, bid.price as lumpSumPrice, (select ifnull(sum(original_price),0) from claim_bid where bid_id = bidId) as totalPrice, tenant_id as tenantId, (select name from tenant where id = claim.tenant_id) as tenantName, claim_title as claimTitle, (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, claim_parts r where p.id = r.part_id and r.claim_id = claim.id) as partNames, (select first_name from users where id = claim.created_by) as createdUser, (select name_en from status where id = bid.status_id) as status, claim.updated_at as statusDate from claim, bid, claim_parts where claim.id = claim_parts.claim_id and claim.request_id = bid.request_id and bid.supplier_id = :tenant order by bidId desc", nativeQuery = true)
    List<ClaimVO> findClaimsForSupplier(Long tenant, Pageable page);

    Optional<Claim> findByRequest(Long requestId);
}
