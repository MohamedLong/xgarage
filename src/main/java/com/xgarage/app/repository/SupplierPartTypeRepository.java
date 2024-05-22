package com.xgarage.app.repository;

import com.xgarage.app.model.PartType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierPartTypeRepository extends JpaRepository<PartType, Long> {
}