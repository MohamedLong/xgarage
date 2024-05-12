package com.xgarage.app.controller;

import com.xgarage.app.dto.PartDto;
import com.xgarage.app.service.PartService;
import com.xgarage.app.model.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/part")
public class PartController {

    @Autowired
    private PartService partService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllParts() {
        try{
            List<Part> parts = partService.findAllParts();
            if(parts == null) {
                return new ResponseEntity<>("Parts Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(parts);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Parts");
        }
    }

    @GetMapping("/{nameLike}")
    public ResponseEntity<?> getPartsByNameLike(@PathVariable("nameLike") String nameLike) {
        try{
            List<Part> parts = partService.getPartByNameLike(nameLike);
            if(parts == null) {
                return new ResponseEntity<>("Parts Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(parts);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Parts");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePart(@RequestBody Part part) {
        try{
            Part savedPart = partService.savePart(part);
            if(savedPart != null) {
                return ResponseEntity.ok().body(savedPart);
            }
            return ResponseEntity.badRequest().body("Unable to Save Part");
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Part");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePart(@RequestBody Part part) {
        try{
            Part updatedPart = partService.updatePart(part);
            if(updatedPart != null) {
                return ResponseEntity.ok().body(updatedPart);
            }
            return ResponseEntity.badRequest().body("Unable to Update Part");
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Updating Part");
        }
    }
}
