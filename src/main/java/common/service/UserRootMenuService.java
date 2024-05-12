package common.service;


import common.model.UserRootMenu;
import common.repository.UserRootMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRootMenuService {


    @Autowired
    private UserRootMenuRepository userRootMenuRepository;

    public UserRootMenu addUserRootMenu(UserRootMenu userRootMenu) {
        UserRootMenu savedUserRootMenu = userRootMenuRepository.save(userRootMenu);
        if(savedUserRootMenu != null) {
            return savedUserRootMenu;
        }
        return null;
    }

    public UserRootMenu updateUserRootMenu(UserRootMenu userRootMenu) {
        Optional<UserRootMenu> fetchedUserRootMenu = userRootMenuRepository.findById(userRootMenu.getId());
        if(fetchedUserRootMenu.isPresent()) {
            return userRootMenuRepository.save(userRootMenu);
        }
        return null;
    }

    public boolean deleteUserRootMenu(Long userRootMenuId) {
        Optional<UserRootMenu> fetchedUserRootMenu = userRootMenuRepository.findById(userRootMenuId);
        if(fetchedUserRootMenu.isPresent()) {
            userRootMenuRepository.delete(fetchedUserRootMenu.get());
            return true;
        }
        return false;
    }

    public UserRootMenu findUserRootMenuById(Long userRootMenuId) {
        Optional<UserRootMenu> fetchedUserRootMenu = userRootMenuRepository.findById(userRootMenuId);
        return fetchedUserRootMenu.orElse(null);
    }

    public List<UserRootMenu> findAllUserRootMenus() {
        return userRootMenuRepository.findAll();
    }

    public List<UserRootMenu> findUserRootMenuByRole(Long roleId) {
        return userRootMenuRepository.findUserRootMenuByRole(roleId);
    }
}
