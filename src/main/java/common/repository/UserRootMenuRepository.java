package common.repository;

import common.model.UserRootMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRootMenuRepository extends JpaRepository<UserRootMenu, Long> {

    @Query(value = "select * from user_root_menu where role_id = :roleId", nativeQuery = true)
    List<UserRootMenu> findUserRootMenuByRole(Long roleId);

}
