package common.controller;

import common.dto.MessageResponse;
import ip.library.usermanagement.model.TenantType;
import ip.library.usermanagement.service.TenantTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/tenantType")
@Slf4j
public class TenantTypeController {

    @Autowired private TenantTypeService tenantTypeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTenantTypes(){
        try{
            List<TenantType> tenantTypes = tenantTypeService.getAllTenantTypes();
            if(tenantTypes == null) {
                return new ResponseEntity<>("Tenant Types Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(tenantTypes);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error getting Tenant Types", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long tenantTypeId){
        try{
            TenantType tenantType = tenantTypeService.findTenantTypeById(tenantTypeId);
            if(tenantType == null) {
                return new ResponseEntity<>("TenantType Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(tenantType);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error getting Tenant Type", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveTenantType(@RequestBody TenantType tenantType){
        try{
            TenantType dbTenantType = tenantTypeService.saveTenantType(tenantType);
            if(dbTenantType != null) {
                return ResponseEntity.ok(dbTenantType);
            }else {
                return ResponseEntity.badRequest().body("Unable to save Tenant Type");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Tenant Type");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTenantType(@RequestBody TenantType tenantType){
        try{
            TenantType dbTenantType = tenantTypeService.saveTenantType(tenantType);
            if(dbTenantType != null) {
                return ResponseEntity.ok(dbTenantType);
            }else {
                return ResponseEntity.badRequest().body("Unable to save Tenant Type");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Tenant Type");
        }
    }

    @DeleteMapping("/delete/{tenantTypeId}")
    public ResponseEntity<?> deleteTenantType(@PathVariable("tenantTypeId") Long tenantTypeId) {
        try{
            log.info("parameter: " + tenantTypeId);
            tenantTypeService.deleteTenantType(tenantTypeId);
            return ResponseEntity.ok().body(new MessageResponse("Success", HttpStatus.OK.value()));
        }catch(Exception e) {
            log.info("deleteRole Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Error Deleting Tenant Type", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }
}
