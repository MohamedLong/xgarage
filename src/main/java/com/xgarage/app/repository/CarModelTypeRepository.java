package com.xgarage.app.repository;

import com.xgarage.app.model.CarModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarModelTypeRepository extends JpaRepository<CarModelType, Long> {
}
