package common.repository;

import common.model.SubMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubMenuRepository extends JpaRepository<SubMenu, Long> {

    List<SubMenu> findByPageNameContaining(String subMenuName);

    @Query(value = "select * from sub_menu where main_menu_id = :mainId", nativeQuery = true)
    List<SubMenu> findByMainMenuId(Long mainId);
}
