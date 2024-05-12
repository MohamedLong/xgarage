package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.SubCategory;
import com.xgarage.app.repository.SubCategoryRepository;
import com.xgarage.app.model.Part;
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
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private PartServiceImpl partService;

    @Override
    public SubCategory findProxySubCategoryById(Long id){return subCategoryRepository.getById(id);}

    @Override
    public SubCategory findSubCategoryById(Long id){
        Optional<SubCategory> subCategoryOptional = subCategoryRepository.findById(id);
        return subCategoryOptional.orElse(null);
    }

    @Override
    public List<SubCategory> findAllSubCategories(){return subCategoryRepository.findAll();}

    @Override
    public Page<SubCategory> findSubCategoryPage(Pageable pageable){return subCategoryRepository.findAll(pageable);}

    @Override
    public SubCategory saveSubCategory(SubCategory subCategory){return subCategoryRepository.save(subCategory);}

    @Override
    public boolean deleteSubCategoryById(Long id){
        try {
            subCategoryRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addPartToSubCategory(Long partId, Long subCategoryId){
        try{
            Part part = partService.findProxyPartById(partId);
            SubCategory subCategory = findProxySubCategoryById(subCategoryId);
//            subCategory.getParts().add(part);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SubCategory> findSubCategoriesofCategry(Long catId) {
        return subCategoryRepository.findSubCategorisByCategory(catId);
    }
}
