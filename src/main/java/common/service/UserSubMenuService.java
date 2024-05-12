package common.service;

import common.dto.UserSubMenuDto;
import common.model.UserSubMenu;
import common.repository.UserSubMenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserSubMenuService {


    @Autowired
    private UserSubMenuRepository userSubMenuRepository;

    public UserSubMenu addUserSubMenu(UserSubMenu userSubMenu) {
        return userSubMenuRepository.save(userSubMenu);
    }

    public UserSubMenu updateUserSubMenu(UserSubMenu userSubMenu) {
        Optional<UserSubMenu> optionalUserSubMenu = userSubMenuRepository.findById(userSubMenu.getId());
        if(optionalUserSubMenu.isPresent()) {
            UserSubMenu fetchedUserSubMenu = optionalUserSubMenu.get();
            fetchedUserSubMenu.update(userSubMenu);
            return userSubMenuRepository.save(fetchedUserSubMenu);
        }
        return null;
    }

    public boolean deleteUserSubMenu(Long userSubMenuId) {
        Optional<UserSubMenu> fetchedUserSubMenu = userSubMenuRepository.findById(userSubMenuId);
        if(fetchedUserSubMenu.isPresent()) {
            userSubMenuRepository.delete(fetchedUserSubMenu.get());
            return true;
        }
        return false;
    }

    public UserSubMenu findUserSubMenuById(Long userSubMenuId) {
        Optional<UserSubMenu> fetchedUserSubMenu = userSubMenuRepository.findById(userSubMenuId);
        return fetchedUserSubMenu.orElse(null);
    }

    public List<UserSubMenu> findAllUserSubMenus() {
        return userSubMenuRepository.findAll();
    }

    public List<UserSubMenu> findUserSubMenuByRole(Long roleId) {
        return userSubMenuRepository.findUserSubMenuByRole(roleId);
    }

    public List<UserSubMenuDto> findUserSubMenuByMainAndRole(Long menuId, Long roleId) {
        log.info("menu ID: {}",menuId," role ID: {}",roleId);
        List<UserSubMenu> userSubMenus = userSubMenuRepository.findUserSubMenuByMainAndRole(menuId, roleId);
        List<UserSubMenuDto> userSubMenuDtos = new ArrayList<>();
        for(UserSubMenu menu: userSubMenus) {
            UserSubMenuDto dto = new UserSubMenuDto(menu.getId(), menu.getSubMenu(), menu.isNewAuth(), menu.isEditAuth(), menu.isDeleteAuth(), menu.isPrintAuth(), menu.isApproveAuth(), menu.isCancelAuth(), menu.isAcceptAuth(), menu.isCompleteAuth());
            userSubMenuDtos.add(dto);
        }
//        log.info("User Sub Menues {}", userSubMenuDtos);
        return userSubMenuDtos;
    }
}
