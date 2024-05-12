package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.CarModelYearRepository;
import com.xgarage.app.model.CarModelYear;
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
public class CarModelYearServiceImpl implements CarModelYearService {

    @Autowired
    private CarModelYearRepository carModelYearRepository;

    @Override
    public CarModelYear findProxyCarModelYear(Long id){return carModelYearRepository.getById(id);}

    @Override
    public CarModelYear findCarModelYear(Long id){
        Optional<CarModelYear> carModelYearOptional = carModelYearRepository.findById(id);
        return carModelYearOptional.orElse(null);
    }

    @Override
    public List<CarModelYear> findAllCarModelYear(){return carModelYearRepository.findAll();}

    @Override
    public Page<CarModelYear> findCarModelYearPage(Pageable pageable){return carModelYearRepository.findAll(pageable);}

    @Override
    public CarModelYear saveCarModelYear(CarModelYear carModelYear){return carModelYearRepository.save(carModelYear);}

    @Override
    public boolean deleteCarModelYearById(Long id){
        try{
            carModelYearRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
