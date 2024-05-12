package com.xgarage.app.repository;

import com.xgarage.app.dto.ClaimPartVO;
import com.xgarage.app.model.ClaimPartList;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimPartListRepository extends GenericRepository<ClaimPartList> {

    @Query(value = "select part_id as partId, (select name from part where id = partId) as partName, (select category_id from sub_category where id = (select subcategory_id from part where id = partId)) as categoryId, (select subcategory_id from part where id = partId) as subcategoryId, (select name from category where id = categoryId) as categoryName, (select name from sub_category where id = subcategoryId) as subcategoryName from claim_part_list", nativeQuery = true)
    List<ClaimPartVO> findAllClaimParts();

}
