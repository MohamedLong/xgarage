package com.xgarage.app.controller;

import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.model.ClaimSelectedTick;
import com.xgarage.app.repository.ClaimSelectedTickRepository;
import com.xgarage.app.service.ClaimSelectedTickService;
import com.xgarage.app.utils.OperationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core/api/v1/claimSelectedTicks")
public class ClaimSelectedTickController {

    @Autowired
    private ClaimSelectedTickService claimSelectedTickService;

    @Autowired private OperationCode operationCode;

    @PostMapping("/save")
    public ResponseEntity<?> saveAll(@RequestBody ClaimSelectedTick created) {
        try{
            ClaimSelectedTick savedTick = claimSelectedTickService.save(created);
            if(savedTick != null) {
                return ResponseEntity.ok(savedTick);
            }
            return new ResponseEntity<>("Claim Tick Not Saved", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving Claim Tick..", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
