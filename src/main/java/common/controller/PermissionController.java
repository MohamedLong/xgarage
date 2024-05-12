package common.controller;

import ip.library.usermanagement.model.Permission;
import ip.library.usermanagement.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/permission")
@CrossOrigin
@Slf4j
public class PermissionController {
    

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/save")
    public ResponseEntity<?> addPermission(@RequestBody Permission permission) {
        try {
            Permission savedPermission = permissionService.savePermission(permission);
            if(savedPermission != null) {
                savedPermission.setCreatedAt(new Timestamp(new Date().getTime()));
                return ResponseEntity.ok().body(savedPermission);
            }
            return new ResponseEntity<>("Could not Save permission", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addpermission Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving permission", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePermission(@RequestBody Permission permission) {
        try{
            Permission updatedPermission = permissionService.updatePermission(permission);
            if(updatedPermission != null) {
                return ResponseEntity.ok().body(updatedPermission);
            }
            return new ResponseEntity<>("Could not update permission", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updatepermission Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating permission", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{permissionId}")
    public ResponseEntity<?> deletePermission(@PathVariable("permissionId") Long permissionId) {
        try{
            permissionService.deletePermission(permissionId);
            return ResponseEntity.ok().body("Success");
        }catch(Exception e) {
            log.info("deletepermission Error:" + e.getMessage());
            return new ResponseEntity<>("Error Deleting permission", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{permissionId}")
    public ResponseEntity<?> findPermissionById(@PathVariable("permissionId") Long permissionId) {
        try{
            Permission fetchedPermission = permissionService.findPermissionById(permissionId);
            if(fetchedPermission != null) {
                return ResponseEntity.ok().body(fetchedPermission);
            }
            return new ResponseEntity<>("Permission no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findpermissionById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching permission", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPermissions() {
        try{
            List<Permission> permissionList = permissionService.findAllPermission();
            if(permissionList == null) {
                return new ResponseEntity<>("Permissions not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(permissionList);
        }catch(Exception e) {
            log.info("findAllpermissions Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching permissions", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> findPermissionByRole(@PathVariable("roleId") Long roleId) {
        try{
            Set<Permission> fetchedPermissions = permissionService.findPermissionsByRole(roleId);
            if(fetchedPermissions != null && fetchedPermissions.size() > 0) {
                return ResponseEntity.ok().body(fetchedPermissions);
            }
            return new ResponseEntity<>("Permissions not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findpermissionByName Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching permissions", HttpStatus.FORBIDDEN);
        }
    }

}