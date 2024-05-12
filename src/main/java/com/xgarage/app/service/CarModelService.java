package com.xgarage.app.service;

import com.xgarage.app.model.CarModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CarModelService {
    CarModel findProxyCarModelById(Long id);

    CarModel findCarModelById(Long id);

    List<CarModel> findAllCarModels();

    Page<CarModel> findCarModelPage(Pageable pageable);

    CarModel saveCarModel(CarModel carModel);

    boolean deleteCarModelById(Long id);

    boolean addCarModelYearToCarModel(Long carModelYearId, Long carModelId);

    boolean addCarModelTypeToCarModel(Long carModelTypeId, Long carModelId);
}
