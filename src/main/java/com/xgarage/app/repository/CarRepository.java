package com.xgarage.app.repository;

import com.xgarage.app.dto.CarVO;
import com.xgarage.app.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByChassisNumber(String chn);

    @Query(value = "select car.id, brand.brand_name as brandName, car_model.name as carModel, car_model_type.type as carModelType, car_model_year.year as carModelYear, chassis_number as chassisNumber, " +
            " plate_number as plateNumber, gear_type as gearType from car, brand, car_model, car_model_type, car_model_year where" +
            " car.brand_id = brand.id and car.car_model_id = car_model.id and car.car_model_type_id = car_model_type.id and " +
            " car.car_model_year_id = car_model_year.id", nativeQuery = true)
    List<CarVO> findAllCars();
}
