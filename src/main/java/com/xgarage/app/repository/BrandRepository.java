package com.xgarage.app.repository;

import com.xgarage.app.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    public List<Brand> findAllByOrderByBrandNameAsc();
}
