package com.xgarage.app.controller;

import com.xgarage.app.model.ServiceType;
import com.xgarage.app.service.ServiceTypeService;
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
@RequestMapping("/core/api/v1/serviceTypes")
@Slf4j
public class ServiceTypeController {

    @Autowired
    private ServiceTypeService serviceTypeService;


    @GetMapping("/id/{id}")
    public ResponseEntity<?> findServiceTypeById(@PathVariable("id") Long id) {
        try{
            ServiceType fetchedServiceTypes = serviceTypeService.findServiceTypeById(id);
            if(fetchedServiceTypes != null) {
                return ResponseEntity.ok(fetchedServiceTypes);
            }
            return new ResponseEntity<>("Service Type Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("Error: " + e.getMessage());
            return new ResponseEntity<>("Error Fetching Service Type", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllServiceTypes() {
        try{
            List<ServiceType> fetchedServiceTypes = serviceTypeService.getAllServiceTypes();
            if(fetchedServiceTypes != null && fetchedServiceTypes.size() != 0) {
                return ResponseEntity.ok(fetchedServiceTypes);
            }
            return new ResponseEntity<>("Service Types Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("Error: " + e.getMessage());
            return new ResponseEntity<>("Error Fetching Service Types", HttpStatus.FORBIDDEN);
        }
    }

}
