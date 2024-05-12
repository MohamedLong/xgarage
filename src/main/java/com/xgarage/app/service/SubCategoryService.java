package com.xgarage.app.service;

import com.xgarage.app.model.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface SubCategoryService {
    SubCategory findProxySubCategoryById(Long id);

    SubCategory findSubCategoryById(Long id);

    List<SubCategory> findAllSubCategories();

    Page<SubCategory> findSubCategoryPage(Pageable pageable);

    SubCategory saveSubCategory(SubCategory subCategory);

    boolean deleteSubCategoryById(Long id);

    boolean addPartToSubCategory(Long partId, Long subCategoryId);

    List<SubCategory> findSubCategoriesofCategry(Long catId);
}
