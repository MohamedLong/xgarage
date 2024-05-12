package com.xgarage.app.repository;

import com.xgarage.app.dto.PartDto;
import com.xgarage.app.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

//    @Query(value = "select id, name, status, subcategory_id as subCategoryId from part where name like %:partName%", nativeQuery = true)
    List<Part> findByNameContainingIgnoreCaseOrderByName(String partName);
}
