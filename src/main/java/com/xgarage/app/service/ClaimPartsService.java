package com.xgarage.app.service;

import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.model.Claim;
import com.xgarage.app.model.ClaimParts;
import com.xgarage.app.repository.ClaimPartsRepository;
import com.xgarage.app.service.serviceimpl.ClaimServiceImpl;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ClaimPartsService extends GenericService<ClaimParts> {

    @Autowired private PartService partService;
    @Autowired private ClaimServiceImpl claimService;

    @Autowired private ClaimPartsRepository claimPartsRepository;


    public ClaimPartsService(GenericRepository<ClaimParts> repository) {
        super(repository);
    }

    public boolean savedAllClaimParts(UpdateClaimDto multipleClaim, Long createdUser) {
        List<ClaimParts> claimParts = new ArrayList<>();
        Claim claim = multipleClaim.claim();
        multipleClaim.claimPartsDtoList().forEach(c -> {
            ClaimParts claimPart = ClaimParts
                    .builder()
                    .claim(claim)
                    .part(partService.findPartById(c.partId()))
                    .partOption(c.partOption())
                    .build();
            claimPart.setCreatedBy(createdUser);
            claimParts.add(claimPart);
        });
        if(!claimParts.isEmpty()) {
            claimPartsRepository.saveAll(claimParts);
            return true;
        }
        return false;
    }

    public List<ClaimParts> findByClaim(Long claimId) {
        return claimPartsRepository.findByClaim(claimId);
    }

    public ClaimParts saveClaimPart(ClaimParts created) {
        if(created.getClaim() != null) {
            created.setClaim(claimService.get(created.getClaim().getId()));
        }
        if(created.getPart() != null) {
            created.setPart(partService.findPartById(created.getPart().getId()));
        }
        return claimPartsRepository.save(created);
    }
}
