package com.xgarage.app.repository;

import com.xgarage.app.model.Mcq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McqRepository extends JpaRepository<Mcq, Long> {
}
