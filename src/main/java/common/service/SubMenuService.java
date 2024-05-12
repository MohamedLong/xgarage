package common.service;

import common.model.SubMenu;
import common.repository.MainMenuRepository;
import common.repository.SubMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubMenuService {


    @Autowired
    private SubMenuRepository subMenuRepository;

    @Autowired
    private MainMenuRepository mainMenuRepository;

    public SubMenu addSubMenu(SubMenu subMenu) {
        if (subMenu.getMainMenu().getId() != null){
            subMenu.setMainMenu(mainMenuRepository.findById(subMenu.getMainMenu().getId()).get());
        }
        SubMenu savedSubMenu = subMenuRepository.save(subMenu);
        if(savedSubMenu != null) {
            return savedSubMenu;
        }
        return null;
    }

    public SubMenu updateSubMenu(SubMenu subMenu) {
        Optional<SubMenu> fetchedSubMenu = subMenuRepository.findById(subMenu.getId());
        if(fetchedSubMenu.isPresent()) {
            return subMenuRepository.save(subMenu);
        }
        return null;
    }

    public boolean deleteSubMenu(Long subMenuId) {
        Optional<SubMenu> fetchedSubMenu = subMenuRepository.findById(subMenuId);
        if(fetchedSubMenu.isPresent()) {
            subMenuRepository.delete(fetchedSubMenu.get());
            return true;
        }
        return false;
    }

    public SubMenu findSubMenuById(Long subMenuId) {
        Optional<SubMenu> fetchedSubMenu = subMenuRepository.findById(subMenuId);
        return fetchedSubMenu.orElse(null);
    }

    public List<SubMenu> findSubMenuByName(String pageName) {
        List<SubMenu> fetchedSubMenu = subMenuRepository.findByPageNameContaining(pageName);
        return fetchedSubMenu;
    }

    public List<SubMenu> findAllSubMenus() {
        return subMenuRepository.findAll();
    }

    public List<SubMenu> findSubMenuByMainId(Long mainId) {
        List<SubMenu> subMenus = subMenuRepository.findByMainMenuId(mainId);
        return subMenus;
    }
}
