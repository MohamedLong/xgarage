package common.controller;

import common.dto.MessageResponse;
import common.dto.UserSubMenuDto;
import common.model.UserSubMenu;
import common.service.UserMainMenuService;
import common.service.UserSubMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/userSubMenu")
@Slf4j
public class UserSubMenuController {

    @Autowired
    private UserSubMenuService userSubMenuService;

    @Autowired
    private UserMainMenuService userMainMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addUserSubMenu(@RequestBody UserSubMenu userSubMenu) {
        try {
            UserSubMenu savedUserSubMenu = userSubMenuService.addUserSubMenu(userSubMenu);
            if(savedUserSubMenu != null) {
                return ResponseEntity.ok().body(savedUserSubMenu);
            }
            return new ResponseEntity<>("Could not Save UserSubMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addUserSubMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving UserSubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserSubMenu(@RequestBody UserSubMenu userSubMenu) {
        try{
            UserSubMenu updatedUserSubMenu = userSubMenuService.updateUserSubMenu(userSubMenu);
            if(updatedUserSubMenu != null) {
                return ResponseEntity.ok().body(updatedUserSubMenu);
            }
            return new ResponseEntity<>("Could not update UserSubMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateUserSubMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating UserSubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserSubMenu(@PathVariable("id") Long userSubMenuId) {
        try{
            boolean deleted = userSubMenuService.deleteUserSubMenu(userSubMenuId);
            if(deleted) {
                return ResponseEntity.ok().body(new MessageResponse("operation.ok", HttpStatus.OK.value()));
            }
            return new ResponseEntity<>(new MessageResponse("Error deleting UserSubMenu", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteUserSubMenu Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Error Deleting UserSubMenu", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findUserSubMenuById(@PathVariable("id") Long userSubMenuId) {
        try{
            UserSubMenu fetchedUserSubMenu = userSubMenuService.findUserSubMenuById(userSubMenuId);
            if(fetchedUserSubMenu != null) {
                return ResponseEntity.ok().body(fetchedUserSubMenu);
            }
            return new ResponseEntity<>("UserSubMenu no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserSubMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserSubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> findUserSubMenusByRole(@PathVariable("roleId") Long roleId) {
        try{
            List<UserSubMenu> subMenuList = userSubMenuService.findUserSubMenuByRole(roleId);
            if(subMenuList != null) {
//                List<UserSubMenuDto> fetchedUserSubMenus = subMenuList.stream().map(a -> (new UserSubMenuDto(a.getModuleId().getPageId().getId(), a.getModuleId().getPageId().getPageName(), a.getModuleId().getPageId().getUiComponent(), a.getModuleId().getPageId().getRouterLink()))).collect(Collectors.toList());
                return ResponseEntity.ok().body(subMenuList);
            }
            return new ResponseEntity<>("UserSubMenuList not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserSubMenusByRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/module/{roleId}/{mainId}")
    public ResponseEntity<?> findUserSubMenusByMainMenuAndRole(@PathVariable("roleId") Long roleId, @PathVariable("mainId") Long mainId) {
        try{
            List<UserSubMenuDto> subMenuList = userSubMenuService.findUserSubMenuByMainAndRole(mainId, roleId);
            if(subMenuList != null) {
//                List<UserSubMenuDto> fetchedUserSubMenus = subMenuList.stream().map(a -> (new UserSubMenuDto(a.getModuleId().getPageId().getId(), a.getModuleId().getPageId().getPageName(), a.getModuleId().getPageId().getUiComponent(), a.getModuleId().getPageId().getRouterLink()))).collect(Collectors.toList());
                return ResponseEntity.ok().body(subMenuList);
            }
            return new ResponseEntity<>("UserSubMenuList not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserSubMenusByRole Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllUserSubMenus() {
        try{
            List<UserSubMenu> UserSubMenuList = userSubMenuService.findAllUserSubMenus();
            if(UserSubMenuList == null) {
                return new ResponseEntity<>("UserSubMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(UserSubMenuList);
        }catch(Exception e) {
            log.info("findAllUserSubMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserSubMenus", HttpStatus.FORBIDDEN);
        }
    }
}
