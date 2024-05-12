package com.xgarage.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgarage.app.dto.CarVO;
import com.xgarage.app.model.Car;
import com.xgarage.app.model.Request;
import com.xgarage.app.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/car")
@Slf4j
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/{carId}")
    public ResponseEntity<?> getCar(@PathVariable("carId") Long carId){
        try{
            Car fetchedCar = carService.findCarById(carId);
            if(fetchedCar  == null) {
                return new ResponseEntity<>("Car Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(fetchedCar);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Car");
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveCar(@RequestParam("carBody") String carString, @RequestPart(value = "carDocument", required = false) MultipartFile carDocument){
        try{
            Car car = new ObjectMapper().readValue(carString, Car.class);
            Car dbCar = carService.saveFullCar(car, carDocument);
            if(dbCar == null) {
                return new ResponseEntity<>("Could Not Save Car", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(dbCar);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error Saving Car..", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCar(@RequestBody Car car){
        try{
            Car updatedCar = carService.updateCar(car);
            if(updatedCar == null) {
                return new ResponseEntity<>("Could Not Update Car", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(updatedCar);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error Updating Car..", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCars(){
        try{
            List<CarVO> cars = carService.findAllCars();
            if(cars == null) {
                return new ResponseEntity<>("Cars Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(cars);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Cars");
        }
    }

    @GetMapping("/chassisNumber/{chn}")
    public ResponseEntity<?> findCarByChassisNumber(@PathVariable("chn") String chn){
        try{
            Car fetchedCar = carService.findByChassisNumber(chn);
            if(fetchedCar == null) {
                return new ResponseEntity<>("Car Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(fetchedCar);

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Cars");
        }
    }
}
