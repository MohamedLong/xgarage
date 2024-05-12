package common.service;


import common.model.UserMainMenu;
import common.repository.MainMenuRepository;
import common.repository.UserMainMenuRepository;
import common.repository.UserRootMenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserMainMenuService {


    @Autowired
    private UserMainMenuRepository userMainMenuRepository;

    @Autowired
    private UserRootMenuRepository userRootMenuRepository;

    @Autowired
    private MainMenuRepository mainMenuRepository;

    public UserMainMenu addUserMainMenu(UserMainMenu userMainMenu) {
        return userMainMenuRepository.save(userMainMenu);
    }

    public UserMainMenu updateUserMainMenu(UserMainMenu userMainMenu) {
        Optional<UserMainMenu> optionalUserMainMenu = userMainMenuRepository.findById(userMainMenu.getId());
        if(optionalUserMainMenu.isPresent()) {
            UserMainMenu fetchedUserMainMenu = optionalUserMainMenu.get();
            fetchedUserMainMenu.update(userMainMenu);
            return userMainMenuRepository.save(fetchedUserMainMenu);
        }
        return null;
    }

    public boolean deleteUserMainMenu(Long userMainMenuId) {
        Optional<UserMainMenu> fetchedUserMainMenu = userMainMenuRepository.findById(userMainMenuId);
        if(fetchedUserMainMenu.isPresent()) {
            userMainMenuRepository.delete(fetchedUserMainMenu.get());
            return true;
        }
        return false;
    }

    public UserMainMenu findUserMainMenuById(Long userMainMenuId) {
        Optional<UserMainMenu> fetchedUserMainMenu = userMainMenuRepository.findById(userMainMenuId);
        return fetchedUserMainMenu.orElse(null);
    }

    public List<UserMainMenu> findAllUserMainMenus() {
        return userMainMenuRepository.findAll();
    }

    public List<UserMainMenu> findUserMainMenuByRole(Long roleId) {
        return userMainMenuRepository.findByRole(roleId);
    }
}
