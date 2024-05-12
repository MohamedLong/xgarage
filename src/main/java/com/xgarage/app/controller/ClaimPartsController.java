package com.xgarage.app.controller;

import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.model.ClaimParts;
import com.xgarage.app.model.Job;
import com.xgarage.app.service.ClaimPartsService;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.UserHelperService;
import genericlibrary.lib.generic.GenericController;
import genericlibrary.lib.generic.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/core/api/v1/claimParts")
@Slf4j
public class ClaimPartsController extends GenericController<ClaimParts> {

    @Autowired private ClaimPartsService claimPartsService;

    @Autowired private OperationCode operationCode;
    @Autowired private UserHelperService userHelper;

    public ClaimPartsController(GenericService<ClaimParts> service) {
        super(service);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody UpdateClaimDto created) {
        try{
            Long user = userHelper.getAuthenticatedUser();
            if(claimPartsService.savedAllClaimParts(created, user)) {
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }
            return new ResponseEntity<>("Claim Parts Not Saved", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving Claim..", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<?> getForClaim(@PathVariable("claimId") Long claimId) {
        try{
            List<ClaimParts> claimParts = claimPartsService.findByClaim(claimId);
            if(claimParts == null) {
                return operationCode.craftResponse("operation.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(claimParts);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Claims");
        }
    }


    @Override
    public ResponseEntity<?> create(@RequestBody ClaimParts created, @RequestHeader Map<String, String> headers) {
        try{
            Long userId = userHelper.getAuthenticatedUser();
            if (userId != null) {
                created.setCreatedBy(userId);
            }
            ClaimParts savedClaimPart = claimPartsService.saveClaimPart(created);
            if(savedClaimPart == null) {
                return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(savedClaimPart);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
