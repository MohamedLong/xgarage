package common.controller;

import common.dto.UserRootMenuDto;
import common.model.UserRootMenu;
import common.service.UserRootMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard/userRootMenu")
@Slf4j
public class UserRootMenuController {
    
    
    @Autowired
    private UserRootMenuService userRootMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addUserRootMenu(@RequestBody UserRootMenu userRootMenu) {
        try {
            UserRootMenu savedUserRootMenu = userRootMenuService.addUserRootMenu(userRootMenu);
            if(savedUserRootMenu != null) {
                return ResponseEntity.ok().body(savedUserRootMenu);
            }
            return new ResponseEntity<>("Could not Save UserRootMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addUserRootMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving UserRootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserRootMenu(@RequestBody UserRootMenu userRootMenu) {
        try{
            UserRootMenu updatedUserRootMenu = userRootMenuService.updateUserRootMenu(userRootMenu);
            if(updatedUserRootMenu != null) {
                return ResponseEntity.ok().body(updatedUserRootMenu);
            }
            return new ResponseEntity<>("Could not update UserRootMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateUserRootMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating UserRootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserRootMenu(@PathVariable("id") Long userRootMenuId) {
        try{
            boolean deleted = userRootMenuService.deleteUserRootMenu(userRootMenuId);
            if(deleted) {
                return ResponseEntity.ok().body("Success");
            }
            return new ResponseEntity<>("Error deleting UserRootMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteUserRootMenu Error:" + e.getMessage());
            return new ResponseEntity<>("Error Deleting UserRootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findUserRootMenuById(@PathVariable("id") Long userRootMenuId) {
        try{
            UserRootMenu fetchedUserRootMenu = userRootMenuService.findUserRootMenuById(userRootMenuId);
            if(fetchedUserRootMenu != null) {
                return ResponseEntity.ok().body(fetchedUserRootMenu);
            }
            return new ResponseEntity<>("UserRootMenu not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserRootMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserRootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> findUserRootMenuByRole(@PathVariable("roleId") Long roleId) {
        try{
            List<UserRootMenu> userRootMenus = userRootMenuService.findUserRootMenuByRole(roleId);
            if(userRootMenus != null && userRootMenus.size() != 0) {
                List<UserRootMenuDto> fetchedUserRootMenus = userRootMenus.stream().map(a -> (new UserRootMenuDto(a.getRootMenu().getId(), a.getRootMenu().getModuleName(),a.getRootMenu().getIcon(),a.getRootMenu().getPageOrder()))).collect(Collectors.toList());
                return ResponseEntity.ok().body(fetchedUserRootMenus);
            }
            return new ResponseEntity<>("UserRootMenus not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserRootMenuByRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserRootMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllUserRootMenus() {
        try{
            List<UserRootMenu> userRootMenuList = userRootMenuService.findAllUserRootMenus();
            if(userRootMenuList == null || userRootMenuList.size() == 0) {
                return new ResponseEntity<>("UserRootMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(userRootMenuList);
        }catch(Exception e) {
            log.info("findAllUserRootMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserRootMenus", HttpStatus.FORBIDDEN);
        }
    }


}
