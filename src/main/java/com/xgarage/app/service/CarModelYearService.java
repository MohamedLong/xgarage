package com.xgarage.app.service;

import com.xgarage.app.model.CarModelYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CarModelYearService {
    CarModelYear findProxyCarModelYear(Long id);

    CarModelYear findCarModelYear(Long id);

    List<CarModelYear> findAllCarModelYear();

    Page<CarModelYear> findCarModelYearPage(Pageable pageable);

    CarModelYear saveCarModelYear(CarModelYear carModelYear);

    boolean deleteCarModelYearById(Long id);
}
