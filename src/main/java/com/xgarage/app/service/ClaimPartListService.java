package com.xgarage.app.service;

import com.xgarage.app.dto.ClaimPartVO;
import com.xgarage.app.model.ClaimPartList;
import com.xgarage.app.repository.ClaimPartListRepository;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimPartListService extends GenericService<ClaimPartList> {

    @Autowired private ClaimPartListRepository claimPartListRepository;

    public ClaimPartListService(GenericRepository<ClaimPartList> repository) {
        super(repository);
    }

    public List<ClaimPartVO> findAllClaimParts() {
        return claimPartListRepository.findAllClaimParts();
    }
}
