package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.Category;
import com.xgarage.app.model.SubCategory;
import com.xgarage.app.repository.CategoryRepository;
import com.xgarage.app.service.CategoryService;
import com.xgarage.app.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryService subCategoryService;

    @Override
    public Category findProxyCategoryById(Long id){return categoryRepository.getById(id);}

    @Override
    public Category findCategoryById(Long id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.orElse(null);
    }

    @Override
    public List<Category> findAllCategories(){
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Page<Category> findCategoryPage(Pageable pageable){return categoryRepository.findAll(pageable);}

    @Override
    public Category saveCategory(Category category){return categoryRepository.save(category);}

    @Override
    public boolean deleteCategoryById(Long id){
        try{
            categoryRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addSubCategoryToCategory(Long subCategoryId, Long categoryId){
        try{
            SubCategory subCategory = subCategoryService.findProxySubCategoryById(subCategoryId);
            Category category = findProxyCategoryById(categoryId);
//            category.getSubCategories().add(subCategory);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Category findCategoriesById(Long catId) {
        return categoryRepository.findById(catId).orElse(null);
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }
}
