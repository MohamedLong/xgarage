package com.xgarage.app.service;

import com.xgarage.app.model.CarModelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CarModelTypeService {
    CarModelType findProxyCarModelType(Long id);

    CarModelType findCarModelType(Long id);

    List<CarModelType> findAllCarModels();

    Page<CarModelType> findCarModelPage(Pageable pageable);

    CarModelType saveCarModelType(CarModelType carModelType);

    boolean deleteCarModelTypeById(Long id);
}
