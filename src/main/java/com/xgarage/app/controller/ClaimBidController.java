package com.xgarage.app.controller;

import com.xgarage.app.model.ClaimBid;
import com.xgarage.app.service.ClaimBidService;
import com.xgarage.app.utils.OperationCode;
import genericlibrary.lib.generic.GenericController;
import genericlibrary.lib.generic.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/core/api/v1/claimBid")
@Slf4j
public class ClaimBidController extends GenericController<ClaimBid> {

    @Autowired private ClaimBidService claimBidService;
    @Autowired private OperationCode operationCode;


    public ClaimBidController(GenericService<ClaimBid> service) {
        super(service);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<ClaimBid> claimBidList) {
        try{
            claimBidService.saveAll(claimBidList);
            return operationCode.craftResponse("operation.ok", HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bid/{bidId}")
    public ResponseEntity<?> getForBid(@PathVariable("bidId") Long bidId) {
        try {
            List<ClaimBid> claimParts = claimBidService.findByBid(bidId);
            if (claimParts == null) {
                return operationCode.craftResponse("operation.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(claimParts);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Claims");
        }
    }


}
