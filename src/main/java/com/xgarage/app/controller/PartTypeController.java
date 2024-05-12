package com.xgarage.app.controller;

import com.xgarage.app.model.PartType;
import com.xgarage.app.service.PartTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/partTypes")
@Slf4j
public class PartTypeController {

    @Autowired
    private PartTypeService partTypeService;


    @GetMapping("/id/{id}")
    public ResponseEntity<?> findPartTypeById(@PathVariable("id") Long id) {
        try{
            PartType fetchedPartTypes = partTypeService.findPartTypeById(id);
            if(fetchedPartTypes != null) {
                return ResponseEntity.ok(fetchedPartTypes);
            }
            return new ResponseEntity<>("Part Type Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("Error: " + e.getMessage());
            return new ResponseEntity<>("Error Fetching Part Type", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPartTypes() {
        try{
            List<PartType> fetchedPartTypes = partTypeService.getAllPartTypes();
            if(fetchedPartTypes != null && fetchedPartTypes.size() != 0) {
                return ResponseEntity.ok(fetchedPartTypes);
            }
            return new ResponseEntity<>("Part Types Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("Error: " + e.getMessage());
            return new ResponseEntity<>("Error Fetching Part Types", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all/{requestId}")
    public ResponseEntity<?> findAllPartTypesByRequestId(@PathVariable("requestId")Long id) {
        try{
            List<PartType> fetchedPartTypes = partTypeService.getAllPartTypesByRequestId(id);
            if(fetchedPartTypes != null && fetchedPartTypes.size() != 0) {
                return ResponseEntity.ok(fetchedPartTypes);
            }
            return new ResponseEntity<>("Part Types Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("Error: " + e.getMessage());
            return new ResponseEntity<>("Error Fetching Part Types", HttpStatus.FORBIDDEN);
        }
    }

}
