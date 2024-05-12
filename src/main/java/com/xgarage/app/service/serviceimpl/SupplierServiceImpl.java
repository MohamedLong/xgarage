package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.dto.Tenant;
import com.xgarage.app.dto.TenantType;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.*;
import com.xgarage.app.repository.SupplierRepository;
import com.xgarage.app.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired private PartTypeService partTypeService;

    @Autowired
    private BrandService brandService;
    @Autowired
    private RequestService requestService;

    @Autowired private KernelFeign kernelFeign;


    @Autowired
    private SupplierPartTypeService supplierPartTypeService;

    @Override
    public Supplier findProxySupplierById(Long id){return supplierRepository.getReferenceById(id);}

    @Override
    public Supplier findSupplierById(Long id){
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if(supplierOptional.isPresent()) {
            Supplier cleanSupplier = supplierOptional.get();
            Set<Brand> cleanBrands = cleanSupplier.getBrand().stream().map(b -> new Brand(b.getId(), b.getBrandName(), null, null)).collect(Collectors.toSet());
            cleanSupplier.setBrand(null);
            cleanSupplier.setBrand(cleanBrands);
            return cleanSupplier;
        }
        return null;
    }

    @Override
    public List<Supplier> findAllSuppliers(Integer pageNo, Integer pageSize){
        Pageable page = PageRequest.of(pageNo, pageSize);
        return supplierRepository.findAllSuppliers(page);
    }

    @Override
    public Page<Supplier> findSupplierPage(Pageable pageable){return supplierRepository.findAll(pageable);}

    @Override
    public List<Supplier> findAllSuppliersWithNameLike(String name) {
        return supplierRepository.findByNameContaining(name);
    }

    @Override
    public Supplier saveSupplier(Supplier supplier, boolean publicSupplier){
//        try {
        if(publicSupplier) {
            Tenant tenant = new Tenant();
            tenant.setCr(supplier.getCr());
            tenant.setCreatedBy(supplier.getUser());
            tenant.setEnabled(true);
            tenant.setName(supplier.getName());
            Tenant savedTenant = kernelFeign.saveTenant(tenant);
            supplier.setId(savedTenant.getId());
            supplier.setTenant(savedTenant.getId());
        }else{
            supplier.setId(supplier.getTenant());
        }
        if(supplier.getBrand() != null){
            supplier.setBrand(supplier.getBrand().stream().map(b -> brandService.findBrandById(b.getId())).collect(Collectors.toSet()));
        }
        if(supplier.getPartTypes() != null) {
            supplier.setPartTypes(supplier.getPartTypes().stream().map(p -> partTypeService.findPartTypeById(p.getId())).collect(Collectors.toSet()));
        }
        if(supplier.getServiceTypes() != null) {
            supplier.setServiceTypes(supplier.getServiceTypes().stream().map(s -> serviceTypeService.findServiceTypeById(s.getId())).collect(Collectors.toSet()));
        }
        if(supplier.getUser() == null) {
            supplier.setUser(kernelFeign.getTenantAdmin(supplier.getTenant()));
        }
        if(supplier.getUser() != null) {
            kernelFeign.changeUserRole(supplier.getUser(), "ROLE_SUPPLIER");
//                streamBridge.send("userRole-out-0", new ChangeUserRoleEvent(supplier.getUser(), "ROLE_SUPPLIER"));
        }
        return supplierRepository.save(supplier);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            return null;
//        }
    }

    @Override
    public Supplier updateSupplier(Supplier supplier){
        try{
            if(supplier != null && supplier.getId() != null && supplierRepository.existsById(supplier.getId())){
                Supplier dbSupplier = supplierRepository.getReferenceById(supplier.getId());
                if(supplier.getBrand() != null)
                    dbSupplier.setBrand(supplier.getBrand());
                if(supplier.getServiceTypes() != null)
                    dbSupplier.setServiceTypes(supplier.getServiceTypes());
                if(supplier.getPartTypes() != null)
                    dbSupplier.setPartTypes(supplier.getPartTypes());
                if(supplier.getContactName() != null)
                    dbSupplier.setContactName(supplier.getContactName());
                if(supplier.getCr() != null)
                    dbSupplier.setCr(supplier.getCr());
                if(supplier.getLocations() != null && supplier.getLocations().size() > 0)
                    dbSupplier.setLocations(supplier.getLocations());
                if(supplier.getManufacturer() != null)
                    dbSupplier.setManufacturer(supplier.getManufacturer());
                if(supplier.getSpeciality() != null)
                    dbSupplier.setSpeciality(supplier.getSpeciality());
                if(supplier.getVehicleType() != null)
                    dbSupplier.setVehicleType(supplier.getVehicleType());
                if(supplier.getPhoneNumber() != null) {
                    dbSupplier.setPhoneNumber(supplier.getPhoneNumber());
                }
                if(supplier.getEmail() != null) {
                    dbSupplier.setEmail(supplier.getEmail());
                }
                if(supplier.getName() != null) {
                    dbSupplier.setName(supplier.getName());
                }
                return supplierRepository.save(dbSupplier);
            }
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteSupplierById(Long supplierId){
        supplierRepository.deleteById(supplierId);
        return true;
    }

    @Override
    public Supplier findSupplierByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        return supplier.orElse(null);
    }

    @Override
    public Supplier findSupplierByUserId(Long userId) {
        Optional<Supplier> supplier = supplierRepository.findByUserId(userId);
        return supplier.orElse(null);
    }

    @Override
    public Long findSupplierIdByUserId(Long userId) {
        return supplierRepository.findSupplierIdByUserId(userId).orElse(null);
    }

    @Override
    public List<Supplier> findSupplierByCoordinates(BigDecimal lat, BigDecimal longit) {
        try{
            return supplierRepository.findSupplierByCoordinates(lat, longit);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Supplier> findSupplierByServiceType(Long serviceId) {
        try{
            return supplierRepository.findSupplierByServiceType(serviceId);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addUserToSupplier(Long userId, Long supplierId){
        try {
            Supplier supplier = findProxySupplierById(supplierId);
            supplier.setUser(userId);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addServiceTypeToSupplier(Long serviceId, Long supplierId) {
        try{
            ServiceType serviceType = serviceTypeService.findServiceTypeById(serviceId);
            Supplier supplier = findProxySupplierById(supplierId);
            if(serviceType != null && supplier != null) {
                supplier.getServiceTypes().add(serviceType);
                return true;
            }
            return false;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addPartTypeToSupplier(Long typeId, Long supplierId) {
        try{
            PartType partType = supplierPartTypeService.getById(typeId);
            Supplier supplier = findProxySupplierById(supplierId);
            supplier.getPartTypes().add(partType);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addNotInterestedSupplierToRequest(Long requestId, Long supplierId) {
        try{
            Request request = requestService.findRequestById(requestId);
            Supplier supplier = findProxySupplierById(supplierId);
            request.getNotInterestedSuppliers().add(supplier);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Supplier> findSuppliersByBrand(Long brandId) {
        try{
            return supplierRepository.findSupplierByBrand(brandId);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Supplier> findNotInterestedSuppliersForRequest(Long requestId) {
        try{
            return supplierRepository.findNotInterestedSuppliersForRequest(requestId);
        }catch(Exception e) {
            log.info("Inside findNotInterestedSuppliersForRequest: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Long supplierId) {
        return supplierRepository.existsById(supplierId);
    }

    @Override
    public Supplier findByShop(Long shopId) {
        return supplierRepository.findByShop(shopId).orElse(null);
    }

    @Override
    public boolean checkSupplierStatus(Long userId) {
        return supplierRepository.checkSupplierStatus(userId);
    }

    @Override
    public boolean deleteSupplier(Supplier supplier) {
        supplierRepository.delete(supplier);
        return true;
    }

    @Override
    public List<Long> getUserIdListFromSupplierIdList(List<Long> supplierList) {
        return supplierRepository.getUserIdListFromSupplierIdList(supplierList);
    }

    @Override
    public Long findUserIdBySupplierId(Long supplierId) {
        return supplierRepository.findUserIdBySupplierId(supplierId).orElse(null);
    }

    @Override
    public List<Supplier> findAllSellers() {
        return supplierRepository.findAllSellers();
    }

    @Override
    public List<Supplier> findByBestSeller(int i) {
        return supplierRepository.findByBestSeller(i);
    }

    @Override
    public List<String> findSellerBrands(Long supplierId) {
        return supplierRepository.findSellerBrands(supplierId);
    }

    @Override
    public void changeSupplierStatus(Long supplierId, boolean status) {
        supplierRepository.getReferenceById(supplierId).setEnabled(status);
    }

    @Override
    public Supplier findSupplierByTenant(Long tenant) {
        return supplierRepository.findByTenant(tenant).orElse(null);
    }

    @Override
    public boolean checkSupplierById(Long supplierId) {
        return supplierRepository.existsById(supplierId);
    }

    @Override
    public List<Long> findSuppliersUserIdListByBrand(Long brandId) {
        return supplierRepository.findSuppliersUserIdListByBrand(brandId);
    }

}
