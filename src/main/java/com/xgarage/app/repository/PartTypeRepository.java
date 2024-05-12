package com.xgarage.app.repository;

import com.xgarage.app.model.PartType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartTypeRepository extends JpaRepository<PartType, Long> {

    @Query(value = "select pt.id as part_type_id, pt.* from request_part_types rpt left join part_type pt on rpt.part_type_id = pt.id left join request r on rpt.request_id = r.id where r.id = :requestId", nativeQuery = true)
    List<PartType> findAllPartTypesByRequestId(@Param("requestId") Long requestId);
}
