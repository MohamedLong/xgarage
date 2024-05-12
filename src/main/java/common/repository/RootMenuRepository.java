package common.repository;

import common.model.RootMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RootMenuRepository extends JpaRepository<RootMenu, Long> {
    List<RootMenu> findByModuleNameContaining(String rootMenuName);
}
