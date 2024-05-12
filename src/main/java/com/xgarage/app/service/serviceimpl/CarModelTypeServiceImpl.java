package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.CarModelType;
import com.xgarage.app.repository.CarModelTypeRepository;
import com.xgarage.app.service.CarModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarModelTypeServiceImpl implements CarModelTypeService {

    @Autowired
    private CarModelTypeRepository carModelTypeRepository;

    @Override
    public CarModelType findProxyCarModelType(Long id){return carModelTypeRepository.getById(id);}

    @Override
    public CarModelType findCarModelType(Long id){
        Optional<CarModelType> carModelTypeOptional = carModelTypeRepository.findById(id);
        return carModelTypeOptional.orElse(null);
    }

    @Override
    public List<CarModelType> findAllCarModels(){return carModelTypeRepository.findAll();}

    @Override
    public Page<CarModelType> findCarModelPage(Pageable pageable){return carModelTypeRepository.findAll(pageable);}

    @Override
    public CarModelType saveCarModelType(CarModelType carModelType){return carModelTypeRepository.save(carModelType);}

    @Override
    public boolean deleteCarModelTypeById(Long id){
        try{
            carModelTypeRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
