package com.xgarage.app.controller;

import com.xgarage.app.model.CarModelYear;
import com.xgarage.app.service.CarModelYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/carModelYear")
public class CarModelYearController {

    @Autowired
    private CarModelYearService carModelYearService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCarModelYears() {
        try{
            List<CarModelYear> carModelYears = carModelYearService.findAllCarModelYear();
            if(carModelYears == null) {
                return new ResponseEntity<>("Car Model Years Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(carModelYears);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car Model Years");
        }
    }
}
