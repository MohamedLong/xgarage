package com.xgarage.app.repository;

import com.xgarage.app.model.ClaimSelectedTick;
import com.xgarage.app.model.ClaimTickId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimSelectedTickRepository extends JpaRepository<ClaimSelectedTick, ClaimTickId> {
}
