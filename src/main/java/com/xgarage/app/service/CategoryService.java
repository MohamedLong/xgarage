package com.xgarage.app.service;

import com.xgarage.app.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CategoryService {
    Category findProxyCategoryById(Long id);

    Category findCategoryById(Long id);

    List<Category> findAllCategories();

    Page<Category> findCategoryPage(Pageable pageable);

    Category saveCategory(Category category);

    boolean deleteCategoryById(Long id);

    boolean addSubCategoryToCategory(Long subCategoryId, Long categoryId);

    Category findCategoriesById(Long catId);

    Category updateCategory(Category category);
}
