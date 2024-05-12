package com.xgarage.app.repository;

import com.xgarage.app.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query(value = "select * from sub_category where category_id = :catId", nativeQuery = true)
    List<SubCategory> findSubCategorisByCategory(Long catId);
}
