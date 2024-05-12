package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.BrandRepository;
import com.xgarage.app.model.Brand;
import com.xgarage.app.model.CarModel;
import com.xgarage.app.service.BrandService;
import com.xgarage.app.service.CarModelService;
import com.xgarage.app.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CarModelService carModelService;
    private final DocumentService documentService;

    @Override
    public Brand findProxyBrandById(Long id){return brandRepository.getById(id);}

    @Override
    public Brand findBrandById(Long id){
        Optional<Brand> brandOptional = brandRepository.findById(id);
        return brandOptional.orElse(null);
    }

    @Override
    public List<Brand> findAllBrands(){return brandRepository.findAllByOrderByBrandNameAsc();}

    @Override
    public Page<Brand> findBrandPage(Pageable page){return brandRepository.findAll(page);}

    @Override
    public Brand saveFakeBrand(Brand brand){return brandRepository.save(brand);}

    @Override
    public Brand saveBrand(Brand brand, MultipartFile file, HttpServletRequest request){
        try {
            brand.setDocument(documentService.saveDocument(file));
            return brandRepository.save(brand);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteBrandById(Long id){
        try{
            if(brandRepository.existsById(id)) {
                Brand brand = findBrandById(id);
                if (brand.getDocument() != null)
                    documentService.deleteDocumentById(brand.getDocument().getId());
                brandRepository.delete(brand);
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
    public boolean addCarModelToBrand(Long carModelId, Long brandId){
        try{
            CarModel carModel = carModelService.findProxyCarModelById(carModelId);
            Brand brand = findProxyBrandById(brandId);
            brand.getCarModels().add(carModel);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
