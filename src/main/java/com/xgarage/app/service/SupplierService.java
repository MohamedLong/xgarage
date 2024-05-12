package com.xgarage.app.service;

import com.xgarage.app.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public interface SupplierService {
    Supplier findProxySupplierById(Long id);

    Supplier findSupplierById(Long id);

    List<Supplier> findAllSuppliers(Integer pageNo, Integer pageSize);

    Page<Supplier> findSupplierPage(Pageable pageable);

    List<Supplier> findAllSuppliersWithNameLike(String name);

    Supplier saveSupplier(Supplier supplier, boolean publicSupplier);

    Supplier updateSupplier(Supplier supplier);

    boolean deleteSupplierById(Long supplierId);

    Supplier findSupplierByName(String name);

    Supplier findSupplierByUserId(Long userId);

    Long findSupplierIdByUserId(Long userId);

    List<Supplier> findSupplierByCoordinates(BigDecimal lat, BigDecimal longit);

    List<Supplier> findSupplierByServiceType(Long serviceId);

    boolean addUserToSupplier(Long userId, Long supplierId);

    boolean addServiceTypeToSupplier(Long serviceId, Long supplierId);

    boolean addPartTypeToSupplier(Long typeId, Long supplierId);

    boolean addNotInterestedSupplierToRequest(Long requestId, Long supplierId);

    List<Supplier> findSuppliersByBrand(Long brandId);

    List<Supplier> findNotInterestedSuppliersForRequest(Long requestId);

    boolean existsById(Long supplierId);

    Supplier findByShop(Long shopId);

    boolean checkSupplierStatus(Long userId);

    boolean deleteSupplier(Supplier supplier);

    List<Long> getUserIdListFromSupplierIdList(List<Long> supplierList);

    Long findUserIdBySupplierId(Long supplierId);

    List<Supplier> findAllSellers();

    List<Supplier> findByBestSeller(int i);

    List<String> findSellerBrands(Long supplierId);

    void changeSupplierStatus(Long supplierId, boolean status);

    Supplier findSupplierByTenant(Long tenant);

    boolean checkSupplierById(Long supplierId);

    List<Long> findSuppliersUserIdListByBrand(Long brandId);
}
