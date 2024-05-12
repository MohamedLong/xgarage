package com.xgarage.app.controller;

import com.xgarage.app.model.SubCategory;
import com.xgarage.app.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/subcategory")
public class SubCategoryController {

    @Autowired
    private SubCategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubCategories() {
        try{
            List<SubCategory> subCategoryList = categoryService.findAllSubCategories();
            if(subCategoryList.isEmpty()) {
                return new ResponseEntity<>("Sub Categories Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(subCategoryList);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Sub Categories.");
        }
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getSubCategoriesByCategory(@PathVariable("id") Long catId) {
        try{
            List<SubCategory> subCategoryList = categoryService.findSubCategoriesofCategry(catId);
            if(subCategoryList == null) {
                return new ResponseEntity<>("Sub Categories Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(subCategoryList);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Sub Categories.");
        }
    }
}
