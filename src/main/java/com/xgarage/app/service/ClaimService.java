package com.xgarage.app.service;

import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.event.ApproveClaimEvent;
import com.xgarage.app.event.ClaimEvent;
import com.xgarage.app.model.Claim;
import com.xgarage.app.model.Status;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClaimService {
    List<ClaimVO> findAllClaims(Integer pageNo, Integer pageSize);

    List<ClaimVO> findByTenant(Long tenantId, Integer pageNo, Integer pageSize);

    @Transactional(rollbackFor = {Exception.class, IOException.class})
    boolean changeStatus(Long id, Long updatedUser, Status newStatus);

    boolean cancelRequestOfClaim(Claim fetchedClaim);

    boolean existsByClaimNo(String claimNo);

    Claim findByClaimNo(String claimNo);

    @Transactional
    Claim saveClaim(Claim claim, MultipartFile claimFile, MultipartFile carFile);

    @Transactional
    Claim updateClaim(UpdateClaimDto updateClaimDto, MultipartFile claimFile);

    List<ClaimVO> ClaimsForRelatedToSupplier(Long tenant, Integer pageNo, Integer pageSize);

    @Transactional
    @EventListener
    void approveClaimHandler(ApproveClaimEvent event);

    @TransactionalEventListener
    void changeClaimStatusHandler(ClaimEvent event);

    List<ClaimVO> findClaimsForSupplier(Long tenant, Integer pageNo, Integer pageSize);

    Claim findByRequest(Long requestId);
}
