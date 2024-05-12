package common.repository;

import common.model.MainMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainMenuRepository extends JpaRepository<MainMenu, Long> {
    List<MainMenu> findByPageNameContaining(String mainMenuName);

    @Query(value = "select * from main_menu where root_menu_id = :rootId", nativeQuery = true)
    List<MainMenu> findByRootMenuId(Long rootId);

}
