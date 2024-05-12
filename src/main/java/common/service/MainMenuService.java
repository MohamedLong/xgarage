package common.service;

import common.model.MainMenu;
import common.repository.MainMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MainMenuService {


    @Autowired
    private MainMenuRepository mainMenuRepository;


    public MainMenu addMainMenu(MainMenu mainMenu) {
        MainMenu savedMainMenu = mainMenuRepository.save(mainMenu);
        if(savedMainMenu != null) {
            return savedMainMenu;
        }
        return null;
    }

    public MainMenu updateMainMenu(MainMenu MainMenu) {
        Optional<MainMenu> fetchedMainMenu = mainMenuRepository.findById(MainMenu.getId());
        if(fetchedMainMenu.isPresent()) {
            return mainMenuRepository.save(MainMenu);
        }
        return null;
    }

    public boolean deleteMainMenu(Long mainMenuId) {
        Optional<MainMenu> fetchedMainMenu = mainMenuRepository.findById(mainMenuId);
        if(fetchedMainMenu.isPresent()) {
            mainMenuRepository.delete(fetchedMainMenu.get());
            return true;
        }
        return false;
    }

    public MainMenu findMainMenuById(Long mainMenuId) {
        Optional<MainMenu> fetchedMainMenu = mainMenuRepository.findById(mainMenuId);
        return fetchedMainMenu.orElse(null);
    }

    public List<MainMenu> findMainMenuByName(String pageName) {
        List<MainMenu> fetchedMainMenu = mainMenuRepository.findByPageNameContaining(pageName);
        return fetchedMainMenu;
    }

    public List<MainMenu> findMainMenuByRootId(Long rootId) {
        List<MainMenu> mainMenus = mainMenuRepository.findByRootMenuId(rootId);
        return mainMenus;
    }

    public List<MainMenu> findAllMainMenus() {
        return mainMenuRepository.findAll();
    }

}
