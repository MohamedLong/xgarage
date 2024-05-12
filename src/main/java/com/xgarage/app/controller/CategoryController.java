package com.xgarage.app.controller;

import com.xgarage.app.model.Category;
import com.xgarage.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/core/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories(){
        try{
            List<Category> categories = categoryService.findAllCategories();
            if(categories == null) {
                return new ResponseEntity<>("Categories Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(categories);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Categories.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllCategoriesById(@PathVariable("id") Long catId){
        try{
            Category category = categoryService.findCategoriesById(catId);
            if(category == null) {
                return new ResponseEntity<>("Categories Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(category);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Categories.");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCategory(@RequestBody Category category){
        try{
            Category dbCategory = categoryService.saveCategory(category);
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/save").toUriString());
            if(dbCategory != null) {
                return ResponseEntity.created(uri).body(dbCategory);
            }
            return ResponseEntity.badRequest().body("Unable to process your request");
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Category.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCategory(@RequestBody Category category){
        try{
            Category dbCategory = categoryService.updateCategory(category);
            if(dbCategory != null) {
                return ResponseEntity.ok().body(dbCategory);
            }
            return ResponseEntity.badRequest().body("Unable to update category");
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error updating Category.");
        }
    }

}
