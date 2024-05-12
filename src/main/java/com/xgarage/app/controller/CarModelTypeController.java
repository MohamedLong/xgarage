package com.xgarage.app.controller;

import com.xgarage.app.model.CarModelType;
import com.xgarage.app.service.CarModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/carModelType")
public class CarModelTypeController {

    @Autowired
    private CarModelTypeService carModelTypeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCarModelTypes() {
        try{
            List<CarModelType> carModelTypes = carModelTypeService.findAllCarModels();
            if(carModelTypes == null) {
                return new ResponseEntity<>("Car Model Types Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(carModelTypes);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car Model Types.");
        }
    }

    @GetMapping("/{carModelTypeId}")
    public ResponseEntity<?> getCarModelTypeById(@PathVariable("carModelTypeId") Long carModeTypeId) {
        try{
            CarModelType carModelType = carModelTypeService.findCarModelType(carModeTypeId);
            if(carModelType == null) {
                return new ResponseEntity<>("Car Mode Type Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(carModelType);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car Model Types.");
        }
    }
}
