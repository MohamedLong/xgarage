package common.controller;

import common.utils.OperationCode;
import ip.library.usermanagement.model.Tenant;
import ip.library.usermanagement.model.TenantType;
import ip.library.usermanagement.service.TenantService;
import ip.library.usermanagement.service.TenantTypeService;
import ip.library.usermanagement.service.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenant")
@Slf4j
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantTypeService tenantTypeService;

    @Autowired private UserHelper userHelper;

    @Autowired private OperationCode operationCode;

    public static final Long PUBLIC_TENANT_TYPE = 1L;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTenant(){
        try{
            List<Tenant> tenants = tenantService.getAllTenant();
            if(tenants == null) {
                return operationCode.craftResponse("operation.tenant.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(tenants);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> findByTenantType(@PathVariable("type") Long tenantType){
        try{
            List<Tenant> tenants = tenantService.findByTenantType(tenantType);
            if(tenants == null) {
                return operationCode.craftResponse("operation.tenant.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(tenants);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long tenantId){
        try{
            Tenant tenant = tenantService.findTenantById(tenantId);
            if(tenant == null) {
                return operationCode.craftResponse("operation.tenant.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(tenant);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveTenant(@RequestBody Tenant tenant){
        try{
            if(tenant.getTenantType() == null) {
                TenantType tenantType = tenantTypeService.findTenantTypeById(PUBLIC_TENANT_TYPE);
                tenant.setTenantType(tenantType);
            }
            Tenant dbTenant = tenantService.saveTenant(tenant);
            if(dbTenant != null) {
                return ResponseEntity.ok(dbTenant);
            }else {
                return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changeStatus/{tenantId}/{status}")
    public ResponseEntity<?> changeTenantStatus(@PathVariable("tenantId") Long tenantId, @PathVariable("status") boolean status){
        try{
            Tenant fetchedTenant = tenantService.findTenantById(tenantId);
            fetchedTenant.setEnabled(status);
            fetchedTenant.setUpdatedAt(new Date());
            fetchedTenant.setUpdatedBy(userHelper.getAuthUserId());
            Tenant dbTenant = tenantService.updateTenant(fetchedTenant);
            if(dbTenant != null) {
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTenant(@RequestBody Tenant tenant){
        try{
            Tenant dbTenant = tenantService.updateTenant(tenant);
            if(dbTenant != null) {
                return ResponseEntity.ok(dbTenant);
            }
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{tenantId}")
    public ResponseEntity<?> deleteTenant(@PathVariable("tenantId") Long tenantId) {
        try{
            tenantService.deleteTenant(tenantId);
            return operationCode.craftResponse("operation.ok", HttpStatus.OK);
        }catch(Exception e) {
            log.info("deleteRole Error:" + e.getMessage());
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
