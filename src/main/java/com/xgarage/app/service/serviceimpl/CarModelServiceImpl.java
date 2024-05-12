package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.CarModelType;
import com.xgarage.app.repository.CarModelRepository;
import com.xgarage.app.model.CarModel;
import com.xgarage.app.model.CarModelYear;
import com.xgarage.app.service.CarModelService;
import com.xgarage.app.service.CarModelTypeService;
import com.xgarage.app.service.CarModelYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarModelServiceImpl implements CarModelService {

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarModelYearService carModelYearService;

    @Autowired
    private CarModelTypeService carModelTypeService;

    @Override
    public CarModel findProxyCarModelById(Long id){return carModelRepository.getById(id);}

    @Override
    public CarModel findCarModelById(Long id){
        Optional<CarModel> carModelOptional = carModelRepository.findById(id);
        return carModelOptional.orElse(null);
    }

    @Override
    public List<CarModel> findAllCarModels(){return carModelRepository.findAll();}

    @Override
    public Page<CarModel> findCarModelPage(Pageable pageable){return carModelRepository.findAll(pageable);}

    @Override
    public CarModel saveCarModel(CarModel carModel){return carModelRepository.save(carModel);}

    @Override
    public boolean deleteCarModelById(Long id){
        try{
            carModelRepository.deleteById(id);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addCarModelYearToCarModel(Long carModelYearId, Long carModelId){
        try{
            CarModelYear carModelYear = carModelYearService.findProxyCarModelYear(carModelYearId);
            CarModel carModel = findProxyCarModelById(carModelId);
            //carModel.getCarModelYears().add(carModelYear);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addCarModelTypeToCarModel(Long carModelTypeId, Long carModelId){
        try{
            CarModelType carModelType = carModelTypeService.findProxyCarModelType(carModelTypeId);
            CarModel carModel = findProxyCarModelById(carModelId);
            //carModel.getCarModelTypes().add(carModelType);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
