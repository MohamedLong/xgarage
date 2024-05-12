package com.xgarage.app.controller;

import com.xgarage.app.model.CarModel;
import com.xgarage.app.service.CarModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/carmodel")
public class CarModelController {

    @Autowired
    private CarModelService carModelService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCarModels() {
        try{
            List<CarModel> carModels = carModelService.findAllCarModels();
            if(carModels == null) {
                return new ResponseEntity<>("Car Models Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(carModels);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car Models");
        }
    }

    @GetMapping("/{carModelid}")
    public ResponseEntity<?> getCarModelById(@PathVariable("carModelId") Long carModelId) {
        try{
            CarModel carModel = carModelService.findCarModelById(carModelId);
            if(carModel == null) {
                return new ResponseEntity<>("Car Model Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(carModel);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car Model");
        }
    }
}
