package com.xgarage.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.model.Claim;
import com.xgarage.app.model.Status;
import com.xgarage.app.service.serviceimpl.ClaimServiceImpl;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.TenantTypeConstants;
import com.xgarage.app.utils.UserHelperService;
import genericlibrary.lib.generic.GenericController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/core/api/v1/claim")
@Slf4j
public class ClaimController extends GenericController<Claim> {

    @Autowired private ClaimServiceImpl claimService;

    @Autowired private UserHelperService userHelper;

    @Autowired private OperationCode operationCode;

    public ClaimController(ClaimServiceImpl service) {
        super(service);
    }


    @Override
    public ResponseEntity<?> create(@RequestBody Claim created, @RequestHeader Map<String, String> headers) {
        try{
            Long user = userHelper.getAuthenticatedUser();
            Long tenant = userHelper.getTenant();
            created.setCreatedBy(user);
            if(created.getTenant() == null) {
                created.setTenant(tenant);
            }
            Claim createdClaim = claimService.saveClaim(created, null,null);
            if(createdClaim == null) {
                return operationCode.craftResponse("", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(createdClaim);

        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.claim.save.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/saveClaim", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveClaim(@RequestParam(value = "claimBody", required = false) String stringClaim, @RequestParam(value = "claimDocument", required = false) Optional<MultipartFile> claimFile, @RequestParam(value = "carDocument", required = false) Optional<MultipartFile> carFile) {
        try{
            Long tenant = userHelper.getTenant();
            Long userId = userHelper.getAuthenticatedUser();
            Claim claim = new ObjectMapper().readValue(stringClaim, Claim.class);
            if (userId != null) {
                claim.setCreatedBy(userId);
            }
            if(claim.getTenant() == null) {
                claim.setTenant(tenant);
            }
            Claim savedClaim = claimService.saveClaim(claim, claimFile.orElse(null), carFile.orElse(null));
            if(savedClaim == null) {
                return operationCode.craftResponse("operation.claim.save.badrequest", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(savedClaim.getId());
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.claim.save.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/updateClaim", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateClaim(@RequestParam(value = "claimBody", required = false) String claimBody, @RequestParam(value = "claimDocument", required = false) Optional<MultipartFile> claimFile) {
        try{
            Long userId = userHelper.getAuthenticatedUser();
            UpdateClaimDto updateClaimDto = new ObjectMapper().readValue(claimBody, UpdateClaimDto.class);
            updateClaimDto.claim().setUpdatedBy(userId);
            Claim updateClaim = claimService.updateClaim(updateClaimDto, claimFile.orElse(null));
            if(updateClaim == null) {
                return operationCode.craftResponse("operation.claim.save.badrequest", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(updateClaim);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.claim.save.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        try{
            List<ClaimVO> claims = claimService.findAllClaims(pageNo, pageSize);
            if(claims == null) {
                return new ResponseEntity<>("Claims Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(claims);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Claims");
        }
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<?> getAllClaimsByTenant(@PathVariable("tenantId") Long tenantId, @RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<ClaimVO> claims = claimService.findByTenant(tenantId, pageNo, pageSize);
            if(claims == null) {
                return new ResponseEntity<>("Claims Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(claims);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Claims");
        }
    }

    @GetMapping("/tenant")
    public ResponseEntity<?> getAllClaimsByTenant(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long tenant = userHelper.getTenant();
            Long tenantType = userHelper.getTenantType();
            if(tenant != null && TenantTypeConstants.Garage.equals(tenantType) ) {
                List<ClaimVO> claims = claimService.ClaimsForRelatedToSupplier(tenant, pageNo, pageSize);
                if(claims == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(claims);
            }
            if (tenant != null) {
                List<ClaimVO> claims = claimService.findByTenant(tenant, pageNo, pageSize);
                if(claims == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(claims);
            }
            return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Claims");
        }
    }

    @GetMapping("/tenantSupplier")
    public ResponseEntity<?> findBySupplierTenant(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        try{
            Long tenant = userHelper.getTenant();
            if (tenant != null) {
                List<ClaimVO> claims = claimService.findClaimsForSupplier(tenant, pageNo, pageSize);
                if(claims == null) {
                    return operationCode.craftResponse("operation.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(claims);
            }
            return operationCode.craftResponse("operation.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable("id") Long claimId, @RequestBody Status status){
        try{
            Long updatedUser = userHelper.getAuthenticatedUser();
            return ResponseEntity.ok(claimService.changeStatus(claimId, updatedUser, status));
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
