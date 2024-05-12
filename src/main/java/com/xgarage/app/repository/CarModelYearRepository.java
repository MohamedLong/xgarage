package com.xgarage.app.repository;

import com.xgarage.app.model.CarModelYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarModelYearRepository extends JpaRepository<CarModelYear, Long> {
}
