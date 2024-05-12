package common.controller;


import common.model.MainMenu;
import common.service.MainMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/mainMenu")
@CrossOrigin
@Slf4j
public class MainMenuController {

    @Autowired
    private MainMenuService mainMenuService;


    @PostMapping("/save")
    public ResponseEntity<?> addMainMenu(@RequestBody MainMenu mainMenu) {
        try {
            MainMenu savedMainMenu = mainMenuService.addMainMenu(mainMenu);
            if(savedMainMenu != null) {
                return ResponseEntity.ok().body(savedMainMenu);
            }
            return new ResponseEntity<>("Could not Save MainMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("addMainMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Saving MainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMainMenu(@RequestBody MainMenu mainMenu) {
        try{
            MainMenu updatedMainMenu = mainMenuService.updateMainMenu(mainMenu);
            if(updatedMainMenu != null) {
                return ResponseEntity.ok().body(updatedMainMenu);
            }
            return new ResponseEntity<>("Could not update MainMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateMainMenu Error: " + e.getMessage());
            return new ResponseEntity<>("Error Updating MainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{mainMenuId}")
    public ResponseEntity<?> deleteMainMenu(@PathVariable("mainMenuId") Long mainMenuId) {
        try{
            boolean deleted = mainMenuService.deleteMainMenu(mainMenuId);
            if(deleted) {
                return ResponseEntity.ok().body("Success");
            }
            return new ResponseEntity<>("Error deleting MainMenu", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("deleteMainMenu Error:" + e.getMessage());
            return new ResponseEntity<>("Error Deleting MainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/id/{mainMenuId}")
    public ResponseEntity<?> findMainMenuById(@PathVariable("mainMenuId") Long mainMenuId) {
        try{
            MainMenu fetchedMainMenu = mainMenuService.findMainMenuById(mainMenuId);
            if(fetchedMainMenu != null) {
                return ResponseEntity.ok().body(fetchedMainMenu);
            }
            return new ResponseEntity<>("MainMenu no Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findMainMenuById Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenu", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/root/{rootId}")
    public ResponseEntity<?> findMainMenusByRoot(@PathVariable("rootId") Long rootId) {
        try{
            List<MainMenu> mainMenuList = mainMenuService.findMainMenuByRootId(rootId);
            if(mainMenuList == null || mainMenuList.size() == 0) {
                return new ResponseEntity<>("MainMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(mainMenuList);
        }catch(Exception e) {
            log.info("findMainMenusByRoot Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllMainMenus() {
        try{
            List<MainMenu> mainMenuList = mainMenuService.findAllMainMenus();
            if(mainMenuList == null) {
                return new ResponseEntity<>("MainMenus not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(mainMenuList);
        }catch(Exception e) {
            log.info("findAllMainMenus Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/name/{mainMenuName}")
    public ResponseEntity<?> findMainMenuByName(@PathVariable("mainMenuName") String mainMenuName) {
        try{
            List<MainMenu> mainMenuList = mainMenuService.findMainMenuByName(mainMenuName);
            if(mainMenuList == null || mainMenuList.size() == 0) {
                return ResponseEntity.ok().body(mainMenuList);
            }
            return new ResponseEntity<>("MainMenu not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findMainMenuByName Error: " + e.getMessage());
            return new ResponseEntity<>("Error fetching MainMenus", HttpStatus.FORBIDDEN);
        }
    }

}
