package com.xgarage.app.controller;

import com.xgarage.app.model.Brand;
import com.xgarage.app.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/core/api/v1/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBrands(){
        try{
            List<Brand> brands = brandService.findAllBrands();
            if(brands.isEmpty()) {
                return new ResponseEntity<>("Brands Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(brands);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error getting Brands", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBrand(@RequestBody Brand brand, MultipartFile file, HttpServletRequest request){
        try{
            Brand dbBrand = brandService.saveBrand(brand, file, request);
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/save").toUriString());
            if(dbBrand != null) {
                return ResponseEntity.created(uri).body(dbBrand);
            }else {
                return ResponseEntity.badRequest().body("Unable to process your request");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Brand");
        }
    }


}
