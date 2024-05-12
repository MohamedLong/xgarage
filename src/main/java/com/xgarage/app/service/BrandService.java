package com.xgarage.app.service;

import com.xgarage.app.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Transactional
public interface BrandService {
    Brand findProxyBrandById(Long id);

    Brand findBrandById(Long id);

    List<Brand> findAllBrands();

    Page<Brand> findBrandPage(Pageable page);

    Brand saveFakeBrand(Brand brand);

    Brand saveBrand(Brand brand, MultipartFile file, HttpServletRequest request);

    boolean deleteBrandById(Long id);

    boolean addCarModelToBrand(Long carModelId, Long brandId);
}
