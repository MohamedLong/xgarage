package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.dto.CarVO;
import com.xgarage.app.repository.CarRepository;
import com.xgarage.app.model.Car;
import com.xgarage.app.service.CarService;
import com.xgarage.app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DocumentService documentService;

    @Override
    public Car findProxyCarById(Long id){return carRepository.getReferenceById(id);}

    @Override
    public Car findCarById(Long id){
        Optional<Car> carOptional = carRepository.findById(id);
        return carOptional.orElse(null);
    }

    @Override
    public List<CarVO> findAllCars(){return carRepository.findAllCars();}

    @Override
    public Page<Car> findCarPage(Pageable pageable){return carRepository.findAll(pageable);}

    @Override
    public Car saveCar(Car car){return carRepository.save(car);}

    @Override
    public Car saveFullCar(Car car, MultipartFile file){
        if(file != null) {
            car.setDocument(documentService.saveDocument(file));
        }
        return saveCar(car);
    }

    @Override
    public boolean deleteCarById(Long id){
        try{
            if(carRepository.existsById(id)) {
                Car car = findCarById(id);
                if(car.getDocument() != null) {
                    documentService.deleteDocumentById(car.getDocument().getId());
                }
                carRepository.deleteById(id);
                return true;
            }else{
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Car findByChassisNumber(String chn) {
        return carRepository.findByChassisNumber(chn).orElse(null);
    }

    @Override
    @Transactional
    public Car updateCar(Car car) {
        Car fetchedCar = carRepository.getReferenceById(car.getId());
        fetchedCar.setCarModelId(car.getCarModelId());
        fetchedCar.setCarModelTypeId(car.getCarModelTypeId());
        fetchedCar.setCarModelYearId(car.getCarModelYearId());
        fetchedCar.setBrandId(car.getBrandId());
        fetchedCar.setChassisNumber(car.getChassisNumber());
        fetchedCar.setGearType(car.getGearType());
        fetchedCar.setPlateNumber(car.getPlateNumber());
        return carRepository.save(fetchedCar);
    }

}
