package com.xgarage.app.repository;

import com.xgarage.app.model.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);

    @Query(value = "select * from supplier order by id", nativeQuery = true)
    List<Supplier> findAllSuppliers(Pageable pageable);
    @Query(value = "select * from supplier where user_id = :id", nativeQuery = true)
    Optional<Supplier> findByUserId(Long id);

    @Query(value = "select * from supplier where latitude = :lat and longitude = :longit", nativeQuery = true)
    List<Supplier> findSupplierByCoordinates(BigDecimal lat, BigDecimal longit);

    @Query(value = "select * from supplier, service_type where supplier.id = supplier_service_types.supplier_id and " +
            " service_type.id = :serviceId", nativeQuery = true)
    List<Supplier> findSupplierByServiceType(Long serviceId);

    @Query(value = "select * from supplier where name like :name", nativeQuery = true)
    List<Supplier> findByNameIsLike(String name);

    List<Supplier> findByNameContaining(String name);

    @Query(value = "select * from supplier where id in(select supplier_id from supplier_brands where brand_id = :brandId)", nativeQuery = true)
    List<Supplier> findSupplierByBrand(Long brandId);

    @Query(value = "select * from supplier where id in(select supplier_id from supplier_requests_notinterested where request_id = :requestId)", nativeQuery = true)
    List<Supplier> findNotInterestedSuppliersForRequest(Long requestId);

    List<Supplier> findByBestSeller(int bestSeller);
    @Query(value = "select * from supplier where id = (select seller_id from store_shops where id = :shopId)", nativeQuery = true)
    Optional<Supplier> findByShop(Long shopId);

    @Query(value = "select id from supplier where id = (select tenant_id from users where id = :userId)", nativeQuery = true)
    Optional<Long> findSupplierIdByUserId(Long userId);

    @Query(value = "select user_id from supplier where id = :supplierId", nativeQuery = true)
    Optional<Long> findUserIdBySupplierId(Long supplierId);

    @Query(value = "select * from supplier where id in(select seller_id from store_shops)", nativeQuery = true)
    List<Supplier> findAllSellers();

    @Query(value = "select distinct brand_name from brand where id in(select brand_id from supplier_brands where supplier_id = :sellerId)", nativeQuery = true)
    List<String> findSellerBrands(Long sellerId);

    @Query(value = "select enabled from supplier where user_id = :userId", nativeQuery = true)
    boolean checkSupplierStatus(Long userId);

    @Query(value = "select user_id from supplier where id in :supplierList", nativeQuery = true)
    List<Long> getUserIdListFromSupplierIdList(List<Long> supplierList);

    Optional<Supplier> findByTenant(Long tenant);

    @Query(value = "select user_id from supplier where id in(select supplier_id from supplier_brands where brand_id = :brandId)", nativeQuery = true)
    List<Long> findSuppliersUserIdListByBrand(Long brandId);
}
