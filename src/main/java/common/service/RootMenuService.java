package common.service;

import common.model.RootMenu;
import common.repository.RootMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RootMenuService {

    @Autowired
    private RootMenuRepository rootMenuRepository;

    public RootMenu addRootMenu(RootMenu rootMenu) {
        RootMenu savedRootMenu = rootMenuRepository.save(rootMenu);
        if(savedRootMenu != null) {
            return savedRootMenu;
        }
        return null;
    }

    public RootMenu updateRootMenu(RootMenu rootMenu) {
        Optional<RootMenu> fetchedRootMenu = rootMenuRepository.findById(rootMenu.getId());
        if(fetchedRootMenu.isPresent()) {
            return rootMenuRepository.save(rootMenu);
        }
        return null;
    }

    public boolean deleteRootMenu(Long rootMenuId) {
        Optional<RootMenu> fetchedRootMenu = rootMenuRepository.findById(rootMenuId);
        if(fetchedRootMenu.isPresent()) {
            rootMenuRepository.delete(fetchedRootMenu.get());
            return true;
        }
        return false;
    }

    public RootMenu findRootMenuById(Long rootMenuId) {
        Optional<RootMenu> fetchedRootMenu = rootMenuRepository.findById(rootMenuId);
        return fetchedRootMenu.orElse(null);
    }

    public List<RootMenu> findRootMenuByName(String moduleName) {
        List<RootMenu> fetchedRootMenu = rootMenuRepository.findByModuleNameContaining(moduleName);
        return fetchedRootMenu;
    }

    public List<RootMenu> findAllRootMenus() {
        return rootMenuRepository.findAll();
    }
}
