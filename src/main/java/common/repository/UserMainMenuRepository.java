package common.repository;

import common.model.UserMainMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMainMenuRepository extends JpaRepository<UserMainMenu, Long> {

//    @Query(value = "select * from user_main_menu umm, main_menu mm where umm.role_id = :roleId and umm.main_menu_id = mm.id order by mm.page_order", nativeQuery = true)
    List<UserMainMenu> findByRole(Long roleId);
}
