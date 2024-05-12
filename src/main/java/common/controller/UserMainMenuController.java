package common.controller;

import common.dto.MessageResponse;
import common.dto.UserMainMenuDto;
import common.model.UserMainMenu;
import common.service.UserMainMenuService;
import common.service.UserRootMenuService;
import common.service.UserSubMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard/userMainMenu")
@Slf4j
public class UserMainMenuController {

    @Autowired
    private UserMainMenuService userMainMenuService;

    @Autowired
    private UserSubMenuService userSubMenuService;

    @Autowired
    private UserRootMenuService userRootMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addUserMainMenu(@RequestBody UserMainMenu userMainMenu) {
        try {
            log.info("Object of user main menu {}",userMainMenu);
            UserMainMenu savedUserMainMenu = userMainMenuService.addUserMainMenu(userMainMenu);
            if(savedUserMainMenu != null) {
                return ResponseEntity.ok().body(savedUserMainMenu);
            }
            return new ResponseEntity<>("Could not Save UserMainMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addUserMainMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving UserMainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserMainMenu(@RequestBody UserMainMenu userMainMenu) {
        try{
//            UserRootMenu userRootMenu = userRootMenuService.findUserRootMenuById(1L);
//            userMainMenu.setUserRootMenu(userRootMenu);
            UserMainMenu updatedUserMainMenu = userMainMenuService.updateUserMainMenu(userMainMenu);
            if(updatedUserMainMenu != null) {
                return ResponseEntity.ok().body(updatedUserMainMenu);
            }
            return new ResponseEntity<>("Could not update UserMainMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateUserMainMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating UserMainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserMainMenu(@PathVariable("id") Long userMainMenuId) {
        try{
            boolean deleted = userMainMenuService.deleteUserMainMenu(userMainMenuId);
            if(deleted) {
                return ResponseEntity.ok().body(new MessageResponse("operation.ok", HttpStatus.OK.value()));
            }
            return new ResponseEntity<>(new MessageResponse("Error deleting UserMainMenu",HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteUserMainMenu Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Error deleting UserMainMenu", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findUserMainMenuById(@PathVariable("id") Long userMainMenuId) {
        try{
            UserMainMenu fetchedUserMainMenu = userMainMenuService.findUserMainMenuById(userMainMenuId);
            if(fetchedUserMainMenu != null) {
                return ResponseEntity.ok().body(fetchedUserMainMenu);
            }
            return new ResponseEntity<>("UserMainMenu no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findUserMainMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserMainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> findUserMainMenusByRole(@PathVariable("roleId") Long roleId) {
        try{
            List<UserMainMenu> mainMenuList = userMainMenuService.findUserMainMenuByRole(roleId);
            if(mainMenuList != null) {
                List<UserMainMenuDto> fetchedUserMainMenus = mainMenuList
                        .stream()
                        .map(a -> (new UserMainMenuDto(a.getId(), a.getMainMenu().getId(), a.getMainMenu().getPageOrder(), a.getMainMenu().getPageName(), a.getMainMenu().getUiComponent(), a.getMainMenu().getRouterLink(), a.getMainMenu().getIcon(), userSubMenuService.findUserSubMenuByMainAndRole(a.getId(), roleId))))
                        .sorted(Comparator.comparingInt(UserMainMenuDto::getPageOrder))
                        .collect(Collectors.toList());
                return ResponseEntity.ok().body(fetchedUserMainMenus);
            }
            return new ResponseEntity<>("UserMainMenus not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findMainMenusByRoot Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/role/id/{roleId}")
    public ResponseEntity<?> findUserMainMenusByRoleId(@PathVariable("roleId") Long roleId) {
        try{
            List<UserMainMenu> mainMenuList = userMainMenuService.findUserMainMenuByRole(roleId);
            if(mainMenuList != null) {
                return ResponseEntity.ok().body(mainMenuList);
            }
            return new ResponseEntity<>("UserMainMenus not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findMainMenusByRoot Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/route/role/{roleId}")
    public ResponseEntity<?> findUserMainMenusForRoutesByRole(@PathVariable("roleId") Long roleId) {
        try{
            List<UserMainMenu> mainMenuList = userMainMenuService.findUserMainMenuByRole(roleId);
            if(mainMenuList != null) {
//              List<UserMainMenuDto> fetchedUserMainMenus = mainMenuList.stream().map(a -> (new UserMainMenuDto(a.getPageId().getId(), a.getPageId().getPageName(), a.getPageId().getUiComponent(), a.getPageId().getRouterLink(), a.getPageId().getIcon(), userSubMenuService.findUserSubMenuByMainAndRole(a.getId(), roleId)))).collect(Collectors.toList());
                return ResponseEntity.ok().body(mainMenuList);
            }
            return new ResponseEntity<>("UserMainMenus not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findMainMenusByRoot Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> findAllUserMainMenus() {
        try{
            List<UserMainMenu> UserMainMenuList = userMainMenuService.findAllUserMainMenus();
            if(UserMainMenuList == null) {
                return new ResponseEntity<>("UserMainMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(UserMainMenuList);
        }catch(Exception e) {
            log.info("findAllUserMainMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching UserMainMenus", HttpStatus.FORBIDDEN);
        }
    }
}
