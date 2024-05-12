package com.xgarage.app.controller;

import com.xgarage.app.feign.ShopFeign;
import com.xgarage.app.dto.SupplierDto;
import com.xgarage.app.model.Supplier;
import com.xgarage.app.service.BidService;
import com.xgarage.app.service.SupplierService;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.TenantTypeConstants;
import com.xgarage.app.utils.UserHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/core/api/v1/supplier")
@Slf4j
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private BidService bidService;

    @Autowired
    private UserHelperService userHelper;

    @Autowired
    private ShopFeign shopFeign;

    @Autowired
    private MessageSource messageSource;

    @Autowired private OperationCode operationCode;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Supplier supplier) {
        Long tenantId = userHelper.getAuthenticatedSupplierId();
        boolean publicSupplier = false;
        try{
            if(userHelper.getTenant() == null || userHelper.getTenantType() == null) {
                return operationCode.craftResponse("operation.supplier.create.error", HttpStatus.BAD_REQUEST);
            }
            if(supplier.getTenant() == null) {
                supplier.setTenant(tenantId);
            }
            if(TenantTypeConstants.Public.equals(userHelper.getTenantType())) {
                publicSupplier = true;
            }
            Supplier supplier1 = supplierService.findSupplierByTenant(supplier.getTenant());
            if(supplier1 == null){
                supplier.setRegisteredDate(new Date());
                Supplier dbSupplier = supplierService.saveSupplier(supplier, publicSupplier);
                if(dbSupplier != null) {
                    return operationCode.craftResponse("operation.ok", HttpStatus.OK);
                }
                return operationCode.craftResponse("supplier.register.badrequest", HttpStatus.BAD_REQUEST);
            }
            return operationCode.craftResponse("supplier.register.found", HttpStatus.FOUND);
        }catch(Exception e) {
            log.info("signUp Error:" + e.getMessage());
            return operationCode.craftResponse("supplier.register.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSupplier(@RequestBody Supplier supplier){
        try {
            Supplier supplier1 = supplierService.updateSupplier(supplier);
            if(supplier1 != null){
                return ResponseEntity.ok().body(supplier1);
            }else{
                return ResponseEntity.badRequest().body(supplier);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("could not update supplier");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSuppliers(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "20") Integer pageSize){
        try{
            List<Supplier> suppliers = supplierService.findAllSuppliers(pageNo, pageSize);
            if(!suppliers.isEmpty()) {
                List<SupplierDto> supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations(), s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
                return ResponseEntity.ok().body(supplierDtos);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getAllSuppliers Error:" + e.getMessage());
            return new ResponseEntity(" Error Getting Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{supplierList}")
    public ResponseEntity<?> getUserIdListFromSupplierIdList(@PathVariable List<Long> supplierList){
        try{
            List<Long> userIds = supplierService.getUserIdListFromSupplierIdList(supplierList);
            if(!userIds.isEmpty()) {
                return ResponseEntity.ok().body(userIds);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getAllSuppliers Error:" + e.getMessage());
            return new ResponseEntity(" Error Getting Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{supplierId}")
    public ResponseEntity<?> getSupplierById(@PathVariable("supplierId") Long supplierId) {
        try{
            Supplier supplier = supplierService.findSupplierById(supplierId);
            if(supplier != null) {
                supplier.setSubmittedBids(bidService.countBidsBySupplier(supplierId));
                supplier.setCompletedDeals(bidService.countCompletedDealsBySupplier(supplierId));
                supplier.setRating(shopFeign.calculateSellerAvgRating(supplierId));
                return ResponseEntity.ok().body(supplier);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierById Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check/{supplierId}")
    public ResponseEntity<?> checkSupplierById(@PathVariable("supplierId") Long supplierId) {
        try{
            return ResponseEntity.ok().body(supplierService.checkSupplierById(supplierId));
        }catch(Exception e) {
            log.info("getSupplierById Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getSupplierFromShop(@PathVariable("shopId") Long shopId) {
        try{
            Supplier supplier = supplierService.findByShop(shopId);
            if(supplier != null) {
                return ResponseEntity.ok().body(supplier);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierById Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getSupplierByUserId() {
        try {
            Supplier supplier = userHelper.getAuthenticatedSupplier();
            if(supplier != null) {
                SupplierDto supplierDto = new SupplierDto(supplier.getId(), supplier.getUser(), supplier.getName(), supplier.getEmail(), supplier.getCr(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getLocations(), supplier.isEnabled() , bidService.countBidsBySupplier(supplier.getId()), bidService.countCompletedDealsBySupplier(supplier.getId()), 0);
                return ResponseEntity.ok().body(supplierDto);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByName Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dto/id/{supplierId}")
    public ResponseEntity<?> getSupplierByUserIdDto(@PathVariable("supplierId") Long supplierId) {
        try {
            Supplier supplier = supplierService.findSupplierById(supplierId);
            if(supplier != null) {
                SupplierDto supplierDto = new SupplierDto(supplier.getId(), supplier.getUser(), supplier.getName(), supplier.getEmail(), supplier.getCr(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getLocations(), supplier.isEnabled() , bidService.countBidsBySupplier(supplier.getId()), bidService.countCompletedDealsBySupplier(supplier.getId()), 0);
                return ResponseEntity.ok().body(supplierDto);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByName Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/changeStatus/{id}/{status}")
    public ResponseEntity<?> enableSupplier(@PathVariable("id") Long supplierId, @PathVariable("status") boolean status) {
        try{
            supplierService.changeSupplierStatus(supplierId, status);
            return ResponseEntity.ok().body("Success");
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSupplierIdByUserId(@PathVariable("userId") Long userId) {
        try {
            Long supplierId = supplierService.findSupplierIdByUserId(userId);
            if(supplierId != null) {
                return ResponseEntity.ok().body(supplierId);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByName Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/name/{supplierName}")
    public ResponseEntity<?> getSupplierByName(@PathVariable("supplierName") String supplierName) {
        try {
            Supplier supplier = supplierService.findSupplierByName(supplierName);
            if(supplier != null) {
                SupplierDto supplierDto = new SupplierDto(supplier.getId(), supplier.getUser(), supplier.getName(), supplier.getEmail(), supplier.getCr(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getLocations(), supplier.isEnabled() , bidService.countBidsBySupplier(supplier.getId()), bidService.countCompletedDealsBySupplier(supplier.getId()), 0);
                return ResponseEntity.ok().body(supplierDto);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByName Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<?> getSupplierByBrand(@PathVariable("brandId") Long brandId) {
        try {
            List<Supplier> suppliers = supplierService.findSuppliersByBrand(brandId);
            if(suppliers != null) {
                List<SupplierDto> supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations(), s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
                return ResponseEntity.ok().body(supplierDtos);
            }
            return new ResponseEntity<>("Suppliers Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByName Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Supplier", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nameLike/{likeName}")
    public ResponseEntity<?> findSupplierByNameLike(@PathVariable("likeName") String name) {
        try{
            List<Supplier> suppliers = supplierService.findAllSuppliersWithNameLike(name);
            if(!suppliers.isEmpty()) {
                List<SupplierDto> supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations(), s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
                return ResponseEntity.ok().body(supplierDtos);
            }
            return new ResponseEntity<>("Not Matched Suppliers", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByNameLike Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{latitude}/{longitude}")
    public ResponseEntity<?> getSupplierByCoordinate(@PathVariable("latitude") BigDecimal lat, @PathVariable("longitude") BigDecimal longit) {
        try{
            List<Supplier> suppliers = supplierService.findSupplierByCoordinates(lat, longit);
            if(suppliers != null && !suppliers.isEmpty()) {
                List<SupplierDto> supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations(), s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
                return ResponseEntity.ok().body(supplierDtos);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getSupplierByCoordinate Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/serviceType/{serviceTypeId}")
    public ResponseEntity<?> getSupplierByServiceType(@PathVariable("serviceTypeId") Long serviceTypeId) {
        try {
            List<Supplier> suppliers = supplierService.findSupplierByServiceType(serviceTypeId);
            if(!suppliers.isEmpty()) {
                List<SupplierDto> supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations(), s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
                return ResponseEntity.ok().body(supplierDtos);
            }
            return new ResponseEntity<>("Supplier Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.info("getSupplierByServiceType Error:" + e.getMessage());
            return new ResponseEntity<>(" Error Getting Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/seller/all")
    public ResponseEntity<?> findAllSellers() {
        Locale locale = userHelper.getLocaleFromUser();
        try{
            List<Supplier> sellers = supplierService.findAllSellers();
            if(sellers == null) {
                return new ResponseEntity<>(messageSource.getMessage("getall.notfound", null, locale), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(sellers);
        }catch(Exception e) {
            log.error("Error inside SellerController.findAllSellers: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/seller/bestSellers")
    public ResponseEntity<?> findAllBestSellers() {
        Locale locale = userHelper.getLocaleFromUser();
        try{
            List<Supplier> sellers = supplierService.findByBestSeller(1);
            if(sellers == null) {
                return new ResponseEntity<>(messageSource.getMessage("getall.notfound", null, locale), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(sellers);
        }catch(Exception e) {
            log.error("Error inside SellerController.findAllBestSellers: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/seller/brands/{supplierId}")
    public ResponseEntity<?> findSellerBrands(@PathVariable("supplierId") Long supplierId) {
        Locale locale = userHelper.getLocaleFromUser();
        try{
            List<String> brands = supplierService.findSellerBrands(supplierId);
            if(brands == null) {
                return new ResponseEntity<>(messageSource.getMessage("getall.notfound", null, locale), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(brands);
        }catch(Exception e) {
            log.error("Error inside SellerController.findAllBestSellers: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }
}
