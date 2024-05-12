package common.controller;
import common.dto.MessageResponse;
import common.model.RootMenu;
import common.service.RootMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/rootMenu")
@CrossOrigin
@Slf4j
public class RootMenuController {

    @Autowired
    private RootMenuService rootMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addRootMenu(@RequestBody RootMenu RootMenu) {
        try {
            RootMenu savedRootMenu = rootMenuService.addRootMenu(RootMenu);
            if(savedRootMenu != null) {
                return ResponseEntity.ok().body(savedRootMenu);
            }
            return new ResponseEntity<>("Could not Save RootMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addRootMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving RootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRootMenu(@RequestBody RootMenu rootMenu) {
        try{
            RootMenu updatedRootMenu = rootMenuService.updateRootMenu(rootMenu);
            if(updatedRootMenu != null) {
                return ResponseEntity.ok().body(updatedRootMenu);
            }
            return new ResponseEntity<>("Could not update RootMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateRootMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating RootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{rootMenuId}")
    public ResponseEntity<?> deleteRootMenu(@PathVariable("rootMenuId") Long rootMenuId) {
        try{
            boolean deleted = rootMenuService.deleteRootMenu(rootMenuId);
            if(deleted) {
                return ResponseEntity.ok().body("Success");
            }
            return new ResponseEntity<>(new MessageResponse("Error deleting RootMenu", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteRootMenu Error:" + e.getMessage());
            return new ResponseEntity<>("Error Deleting RootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{rootMenuId}")
    public ResponseEntity<?> findRootMenuById(@PathVariable("rootMenuId") Long rootMenuId) {
        try{
            RootMenu fetchedRootMenu = rootMenuService.findRootMenuById(rootMenuId);
            if(fetchedRootMenu != null) {
                return ResponseEntity.ok().body(fetchedRootMenu);
            }
            return new ResponseEntity<>("RootMenu no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findRootMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching RootMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllRootMenus() {
        try{
            List<RootMenu> rootMenuList = rootMenuService.findAllRootMenus();
            if(rootMenuList == null || rootMenuList.size() == 0) {
                return new ResponseEntity<>("RootMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(rootMenuList);
        }catch(Exception e) {
            log.info("findAllRootMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching RootMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/name/{rootMenuName}")
    public ResponseEntity<?> findRootMenuByName(@PathVariable("rootMenuName") String rootMenuName) {
        try{
            List<RootMenu> rootMenuList = rootMenuService.findRootMenuByName(rootMenuName);
            if(rootMenuList == null || rootMenuList.size() == 0) {
                return ResponseEntity.ok().body(rootMenuList);
            }
            return new ResponseEntity<>("RootMenu not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findRootMenuByName Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching RootMenus", HttpStatus.FORBIDDEN);
        }
    }

}
