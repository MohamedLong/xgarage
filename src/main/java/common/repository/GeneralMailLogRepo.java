package common.repository;

import common.model.GeneralMailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralMailLogRepo extends JpaRepository<GeneralMailLog, Long> {
}
