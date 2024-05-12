package common.controller;

import common.model.SubMenu;
import common.service.SubMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/dashboard/subMenu")
@CrossOrigin
@Slf4j
public class SubMenuController {

    @Autowired
    private SubMenuService subMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addSubMenu(@RequestBody SubMenu subMenu) {
        try {
            SubMenu savedSubMenu = subMenuService.addSubMenu(subMenu);
            if(savedSubMenu != null) {
                return ResponseEntity.ok().body(savedSubMenu);
            }
            return new ResponseEntity<>("Could not Save SubMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addSubMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving SubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSubMenu(@RequestBody SubMenu subMenu) {
        try{
            SubMenu updatedSubMenu = subMenuService.updateSubMenu(subMenu);
            if(updatedSubMenu != null) {
                return ResponseEntity.ok().body(updatedSubMenu);
            }
            return new ResponseEntity<>("Could not update SubMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateSubMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating SubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{subMenuId}")
    public ResponseEntity<?> deleteSubMenu(@PathVariable("subMenuId") Long subMenuId) {
        try{
            boolean deleted = subMenuService.deleteSubMenu(subMenuId);
            if(deleted) {
                return ResponseEntity.ok().body("Success");
            }
            return new ResponseEntity<>("Error deleting SubMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteSubMenu Error:" + e.getMessage());
            return new ResponseEntity<>("Error Deleting SubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{subMenuId}")
    public ResponseEntity<?> findSubMenuById(@PathVariable("subMenuId") Long subMenuId) {
        try{
            SubMenu fetchedSubMenu = subMenuService.findSubMenuById(subMenuId);
            if(fetchedSubMenu != null) {
                return ResponseEntity.ok().body(fetchedSubMenu);
            }
            return new ResponseEntity<>("SubMenu no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findSubMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching SubMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/main/{mainId}")
    public ResponseEntity<?> findSubMenusByMain(@PathVariable("mainId") Long mainId) {
        try{
            List<SubMenu> subMenuList = subMenuService.findSubMenuByMainId(mainId);
            if(subMenuList == null) {
                return new ResponseEntity<>("SubMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(subMenuList);
        }catch(Exception e) {
            log.info("findSubMenusByMain Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching SubMenus", HttpStatus.FORBIDDEN);
        }
    }



    @GetMapping("/all")
    public ResponseEntity<?> findAllSubMenus() {
        try{
            List<SubMenu> SubMenuList = subMenuService.findAllSubMenus();
            if(SubMenuList == null || SubMenuList.size() == 0) {
                return new ResponseEntity<>("SubMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(SubMenuList);
        }catch(Exception e) {
            log.info("findAllSubMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching SubMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/name/{subMenuName}")
    public ResponseEntity<?> findSubMenuByName(@PathVariable("subMenuName") String subMenuName) {
        try{
            List<SubMenu> SubMenuList = subMenuService.findSubMenuByName(subMenuName);
            if(SubMenuList == null || SubMenuList.size() == 0) {
                return ResponseEntity.ok().body(SubMenuList);
            }
            return new ResponseEntity<>("SubMenu not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findSubMenuByName Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching SubMenus", HttpStatus.FORBIDDEN);
        }
    }
}
