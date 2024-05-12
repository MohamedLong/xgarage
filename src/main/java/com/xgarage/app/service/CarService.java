package com.xgarage.app.service;

import com.xgarage.app.dto.CarVO;
import com.xgarage.app.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public interface CarService {
    Car findProxyCarById(Long id);

    Car findCarById(Long id);

    List<CarVO> findAllCars();

    Page<Car> findCarPage(Pageable pageable);

    Car saveCar(Car car);

    Car saveFullCar(Car car, MultipartFile file);

    boolean deleteCarById(Long id);

    Car findByChassisNumber(String chn);

    Car updateCar(Car car);
}
