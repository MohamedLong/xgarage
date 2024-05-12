package common.controller;

import common.dto.MessageResponse;
import ip.library.usermanagement.model.Permission;
import ip.library.usermanagement.model.Role;
import ip.library.usermanagement.service.PermissionService;
import ip.library.usermanagement.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/role")
@CrossOrigin
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/save")
    public ResponseEntity<?> addRole(@RequestBody Role role) {
        try {
            System.out.println("out -> "+role);
            Set<Permission> perms = new HashSet<>();
            Role savedRole = null;
            if(role.getPermissions() != null && role.getPermissions().stream().allMatch(perm -> perm.getId()!=null)) {
                perms = role.getPermissions();
                role.setPermissions(new HashSet<>());
                role.setCreatedAt(new Timestamp(new Date().getTime()));
                savedRole = roleService.saveRole(role);
                savedRole.getPermissions().addAll(perms);
                savedRole = roleService.updateRole(savedRole);
            }else{
                savedRole = roleService.saveRole(role);
            }
            if(savedRole != null) {
                return ResponseEntity.ok().body(savedRole);
            }
            return new ResponseEntity<>("Could not Save Role", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving Role", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/addPermission/{roleId}/{permissionId}")
    public ResponseEntity<?> addPermissionToRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
        try{
            Role fetchedRole = roleService.findRoleById(roleId);
            if(fetchedRole == null) {
                log.info("addPermissionToRole Error: Role Not Found");
                return new ResponseEntity<>("Error Add Permission To Role", HttpStatus.NOT_FOUND);
            }
            Permission fetchedPermission = permissionService.findPermissionById(permissionId);
            if(fetchedPermission == null) {
                log.info("addPermissionToRole Error: Permission Not Found");
                return new ResponseEntity<>("Error Add Permission To Role", HttpStatus.NOT_FOUND);
            }
            roleService.addPermissionToRole(permissionId, roleId);
            return ResponseEntity.ok("Success");
        }catch(Exception e) {
            log.info("addPermissionToRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error Add Permission To Role", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/revokePermission/{roleId}/{permissionId}")
    public ResponseEntity<?> revokePermissionFromRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
        try{
            Role fetchedRole = roleService.findRoleById(roleId);
            if(fetchedRole == null) {
                log.info("addPermissionToRole Error: Role Not Found");
                return new ResponseEntity<>("Error Add Permission To Role", HttpStatus.NOT_FOUND);
            }
            Permission fetchedPermission = permissionService.findPermissionById(permissionId);
            if(fetchedPermission == null) {
                log.info("addPermissionToRole Error: Permission Not Found");
                return new ResponseEntity<>("Error Add Permission To Role", HttpStatus.NOT_FOUND);
            }
            roleService.revokePermissionFromRole(permissionId, roleId);
            return ResponseEntity.ok("Success");
        }catch(Exception e) {
            log.info("revokePermissionToRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error Revoking Permission From Role", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRole(@RequestBody Role role) {
        try{
            Role updatedRole = roleService.updateRole(role);
            if(updatedRole != null) {
                return ResponseEntity.ok().body(updatedRole);
            }
            return new ResponseEntity<>("Could not update Role", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating Role", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable("roleId") Long roleId) {
        try{
            roleService.deleteRoleById(roleId);
            return ResponseEntity.ok().body(new MessageResponse("operation.ok", HttpStatus.OK.value()));
        }catch(Exception e) {
            log.info("deleteRole Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Error Deleting Role", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{roleId}")
    public ResponseEntity<?> findRoleById(@PathVariable("roleId") Long roleId) {
        try{
            Role fetchedRole = roleService.findRoleById(roleId);
            if(fetchedRole != null) {
                return ResponseEntity.ok().body(fetchedRole);
            }
            return new ResponseEntity<>("Role no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findRoleById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching Role", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllRoles() {
        try{
            List<Role> RoleList = roleService.findAllRoles();
            if(RoleList == null) {
                return new ResponseEntity<>("Roles not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(RoleList);
        }catch(Exception e) {
            log.info("findAllRoles Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching Roles", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<?> findRoleByName(@PathVariable("roleName") String roleName) {
        try{
            Role fetchedRole = roleService.findRoleByRoleName(roleName);
            if(fetchedRole != null) {
                return ResponseEntity.ok().body(fetchedRole);
            }
            return new ResponseEntity<>("Role not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findRoleByName Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching Roles", HttpStatus.FORBIDDEN);
        }
    }

}
