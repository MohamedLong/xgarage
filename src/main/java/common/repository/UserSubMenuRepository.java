package common.repository;

import common.model.UserSubMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubMenuRepository extends JpaRepository<UserSubMenu, Long> {

//    @Query(value = "select * from user_sub_menu where role_id = :roleId", nativeQuery = true)
    List<UserSubMenu> findUserSubMenuByRole(Long roleId);

    @Query(value = "select * from user_sub_menu where user_main_menu_id = :menuId and role_id = :roleId", nativeQuery = true)
    List<UserSubMenu> findUserSubMenuByMainAndRole(Long menuId, Long roleId);

    @Query(value = "select * from user_sub_menu where sub_menu_id = :menuId and role_id = :roleId", nativeQuery = true)
    Optional<UserSubMenu> findUserSubMenuBySubAndRole(Long menuId, Long roleId);
}
