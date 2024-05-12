package com.xgarage.app.controller;

import com.xgarage.app.dto.ClaimPartVO;
import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.model.ClaimPartList;
import com.xgarage.app.service.ClaimPartListService;
import genericlibrary.lib.generic.GenericController;
import genericlibrary.lib.generic.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/claimPartList")
public class ClaimPartListController extends GenericController<ClaimPartList> {

    @Autowired private ClaimPartListService claimPartListService;

    public ClaimPartListController(GenericService<ClaimPartList> service) {
        super(service);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllClaimParts() {
        try{
            List<ClaimPartVO> parts = claimPartListService.findAllClaimParts();
            if(parts == null) {
                return new ResponseEntity<>("Parts Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(parts);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Parts");
        }
    }
}
