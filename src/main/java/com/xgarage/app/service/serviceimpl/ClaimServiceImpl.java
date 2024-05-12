package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.dto.ClaimVO;
import com.xgarage.app.dto.UpdateClaimDto;
import com.xgarage.app.event.ApproveClaimEvent;
import com.xgarage.app.event.ClaimEvent;
import com.xgarage.app.event.DirectBidEvent;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.*;
import com.xgarage.app.repository.ClaimRepository;
import com.xgarage.app.service.*;
import com.xgarage.app.utils.RequestStatusConstants;
import genericlibrary.lib.generic.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClaimServiceImpl extends GenericService<Claim> implements ClaimService {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private ClaimSelectedTickService claimTicksService;

    @Autowired private ClaimPartsService claimPartsService;

    @Autowired private RequestService requestService;

    @Autowired private SupplierService supplierService;

    @Autowired private CarService carService;

    @Autowired private DocumentService documentService;

    @Autowired private KernelFeign kernelFeign;

    @Autowired private JobServiceImpl jobService;

    @Autowired private PrincipleService principleService;

    @Autowired private ApplicationEventPublisher publisher;

    @Value("${redirect.mail.address}")
    private String redirectMailAddress;

    public ClaimServiceImpl(ClaimRepository repository) {
        super(repository);
    }


    @Override
    public List<ClaimVO> findAllClaims(Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return claimRepository.findAllClaims(page);
    }

    @Override
    public List<ClaimVO> findByTenant(Long tenantId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return claimRepository.findByTenant(tenantId, page);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean changeStatus(Long id, Long updatedUser, Status newStatus) {
        try{
            Claim fetchedClaim = claimRepository.getReferenceById(id);
            log.info("Inside claimService.changeStatus, fetchedClaim: " + fetchedClaim);
            boolean changed = fetchedClaim.changeStatus(updatedUser, newStatus);
            log.info("Inside claimService.changeStatus, changed: " + changed);
            if(changed && fetchedClaim.getRequest() == null && RequestStatusConstants.CONFIRMED_STATUS == newStatus.getId()) {
                createRequestForClaim(fetchedClaim);
                if(ClaimAssignType.Direct.equals(fetchedClaim.getAssignType()) && fetchedClaim.getAssignedGarage() != null) {
                    publisher.publishEvent(new DirectBidEvent(fetchedClaim.getRequest(), new Date(), fetchedClaim.getAssignedGarage()));
                }
                notifyNewClaim(fetchedClaim);
                return true;
            }
            if(changed && fetchedClaim.getRequest() != null && RequestStatusConstants.CANCELED_STATUS == newStatus.getId()) {
                if(cancelRequestOfClaim(fetchedClaim)) {
                    sendPushNotification(fetchedClaim, "Cancel Claim with Number: " + fetchedClaim.getClaimNo(), "Claim with ID #" + id + " Has Been Canceled.");
                    return true;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            log.info("Inside claimService.changeStatus, changed before return: " + changed);
            return changed;
        }catch(Exception e) {
            e.printStackTrace();
            log.info("Error inside claimService.changeStatus: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    public void notifyNewClaim(Claim fetchedClaim) {
        String keyTitle = "New Claim with Number: " + fetchedClaim.getClaimNo();
        StringBuilder body = new StringBuilder(1000);
        String keyMessage = "New Claim with ID #" + fetchedClaim.getId() + " Has Been Submitted.\n";
        body.append(keyMessage);
        body.append("Please click on the following link to go to the claim details in X Garage:\n");
        body.append(redirectMailAddress + "#/claim-details?id=" + fetchedClaim.getId());
        sendPushNotification(fetchedClaim, keyTitle, body.toString());
    }

    public void createRequestForClaim(Claim fetchedClaim) {
        Request request = Request
                .builder()
                .requestType(RequestKind.Service)
                .car(fetchedClaim.getCar())
                .bidClosingDate((Timestamp) fetchedClaim.getBidClosingDate())
                .description(fetchedClaim.getNotes())
                .locationName(fetchedClaim.getOfficeLocation())
                .privacy(fetchedClaim.getPrivacy())
                .tenant(fetchedClaim.getTenant())
                .user(fetchedClaim.getUpdatedBy())
                .build();
        if(fetchedClaim.getSuppliers() != null && !fetchedClaim.getSuppliers().isEmpty()) {
            fetchedClaim.getSuppliers().forEach(s -> {
                request.getSuppliers().add(supplierService.findSupplierById(s.getId()));
            });
        }
        fetchedClaim.setRequest(requestService.saveRequest(request).getId());
    }

    @Override
    public boolean cancelRequestOfClaim(Claim fetchedClaim) {
        return requestService.cancelRequest(fetchedClaim.getRequest(), fetchedClaim.getUpdatedBy());
    }

    private void sendPushNotification(Claim claim, String keyTitle, String keyMessage) {
        try {
            NotificationEvent multiNotificationEvent = null;
            if(ClaimAssignType.Direct.equals(claim.getAssignType()) && claim.getAssignedGarage() != null) {
                multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(claim.getAssignedGarage()), NotificationType.Claim.name(), claim.getId(), keyTitle, keyMessage, "Private");
            }else if(Privacy.Private.equals(claim.getPrivacy())) {
                multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), claim.getSuppliers().parallelStream().map(Supplier::getUser).collect(Collectors.toList()), NotificationType.Claim.name(), claim.getId(), keyTitle, keyMessage, "Private");
            } else {
                List<Long> supplierUsers = supplierService.findSuppliersUserIdListByBrand(claim.getCar().getBrandId());
                if (supplierUsers != null) {
                    multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), supplierUsers, NotificationType.Claim.name(), claim.getId(), keyTitle, keyMessage, "Public");
                }
            }
            if (multiNotificationEvent != null) {
                kernelFeign.sendPushNotification(multiNotificationEvent);
//                streamBridge.send("notify-out-0", multiNotificationEvent);
            }
        } catch (Exception e) {
            log.info("Exception Inside sendPushNotification, " + e.getMessage());
        }
    }

    @Override
    public boolean existsByClaimNo(String claimNo) {
        return claimRepository.existsByClaimNo(claimNo);
    }

    @Override
    public Claim findByClaimNo(String claimNo) {
        return claimRepository.findByClaimNo(claimNo).orElse(null);
    }

    @Override
    @Transactional
    public Claim saveClaim(Claim claim, MultipartFile claimFile, MultipartFile carFile) {
        try{
            if(claim.getCar() != null) {
                Car dbCar;
                if(claim.getCar().getId() != null) {
                    dbCar = carService.findCarById(claim.getCar().getId());
                }else if(carFile != null) {
                    dbCar = carService.saveFullCar(claim.getCar(), carFile);
                }else{
                    dbCar = carService.saveCar(claim.getCar());
                }
                if(dbCar != null) {
                    claim.setCar(dbCar);
                }
            }
            if(claimFile != null) {
                claim.getDocuments().add(documentService.saveDocument(claimFile));
            }
            if(claim.getPrivacy() == null) {
                claim.setPrivacy(Privacy.Public);
            }
            if(claim.getAssignType() == null) {
                claim.setAssignType(ClaimAssignType.Bidding);
            }
            if(claim.getClaimTicks() != null && !claim.getClaimTicks().isEmpty()) {
                claim.setClaimTicks(claim.getClaimTicks().stream().map(tick -> claimTicksService.save(tick)).toList());
            }
            if(claim.getSuppliers() != null && !claim.getSuppliers().isEmpty()) {
                claim.setSuppliers(claim.getSuppliers().stream().map(s -> supplierService.findSupplierById(s.getId())).collect(Collectors.toList()));
            }
            if(claim.getInspector() != null)  {
                if(claim.getInspector().getId() == null) {
                    claim.setInspector(principleService.create(claim.getInspector()));
                }else{
                    claim.setInspector(principleService.get(claim.getInspector().getId()));
                }
            }
            if(claim.getSurveyer() != null) {
                if(claim.getSurveyer().getId() == null) {
                    claim.setSurveyer(principleService.create(claim.getSurveyer()));
                }else{
                    claim.setSurveyer(principleService.get(claim.getSurveyer().getId()));
                }
            }
            return claimRepository.save(claim);
        }catch(Exception e) {
            log.info("Error inside ClaimService.saveClaim: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public Claim updateClaim(UpdateClaimDto updateClaimDto, MultipartFile claimFile) {
        Claim updatedClaim = update(updateClaimDto.claim());
        if(claimFile != null) {
            updatedClaim.getDocuments().add(documentService.saveDocument(claimFile));
        }
        if(updateClaimDto.claim().getCar() != null) {
            updatedClaim.setCar(carService.findCarById(updateClaimDto.claim().getCar().getId()));
        }
        if(updateClaimDto.claim().getPrivacy() == null) {
            updatedClaim.setPrivacy(Privacy.Public);
        }
        if(updateClaimDto.claim().getAssignType() == null) {
            updatedClaim.setAssignType(ClaimAssignType.Bidding);
        }
        if(updateClaimDto.claim().getClaimTicks() != null && !updateClaimDto.claim().getClaimTicks().isEmpty()) {
            updatedClaim.setClaimTicks(updateClaimDto.claim().getClaimTicks().stream().map(tick -> claimTicksService.save(tick)).toList());
        }
        if(updateClaimDto.claim().getSuppliers() != null && !updateClaimDto.claim().getSuppliers().isEmpty()) {
            updatedClaim.setSuppliers(updateClaimDto.claim().getSuppliers().stream().map(s -> supplierService.findSupplierById(s.getId())).toList());
        }
        if(updateClaimDto.claim().getInspector() != null)  {
            if(updateClaimDto.claim().getInspector().getId() == null) {
                updatedClaim.setInspector(principleService.create(updateClaimDto.claim().getInspector()));
            }else{
                updatedClaim.setInspector(principleService.get(updateClaimDto.claim().getInspector().getId()));
            }
        }
        if(updateClaimDto.claim().getSurveyer() != null) {
            if(updateClaimDto.claim().getSurveyer().getId() == null) {
                updatedClaim.setSurveyer(principleService.create(updateClaimDto.claim().getSurveyer()));
            }else{
                updatedClaim.setSurveyer(principleService.get(updateClaimDto.claim().getSurveyer().getId()));
            }
        }
        claimPartsService.savedAllClaimParts(updateClaimDto, updatedClaim.getCreatedBy());
        return updatedClaim;
    }

    @Override
    public List<ClaimVO> ClaimsForRelatedToSupplier(Long tenant, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return claimRepository.findClaimsForRelatedToSupplier(tenant, page);
    }

    @Override
    @Transactional
    @EventListener
    public void approveClaimHandler(ApproveClaimEvent event) {
        Claim claim = findByRequest(event.requestId());
        if(claim != null) {
            Job job = new Job();
            job.convertFromClaim(claim);
            job.setCreatedAt(LocalDateTime.now());
            job.setCreatedBy(event.updateBy());
            job.setTenant(event.tenantId());
            Job savedJob = jobService.saveJob(job, null);
            log.info("Inside approveClaimHandler, savedJob.id: " + savedJob.getId());
            List<Request> requestList = new ArrayList<>();
            claimPartsService.findByClaim(claim.getId()).forEach(part -> {
                log.info("Inside approveClaimHandler.saveClaimParts Block..");
                Request request = new Request();
                request.setRequestType(RequestKind.Part);
                request.setQty(1.0);
                request.setJob(savedJob.getId());
                request.setUser(savedJob.getCreatedBy());
                request.setTenant(savedJob.getTenant());
                request.setCar(savedJob.getCar());
                request.setPart(part.getPart());
                request.setRequestTitle(savedJob.getJobTitle());
                request.setCar(savedJob.getCar());
                request.setTenant(event.tenantId());
                request.setPrivacy(savedJob.getPrivacy() == null ? Privacy.Public : savedJob.getPrivacy());
                requestList.add(request);
            });
            requestService.saveAll(requestList);
            claim.setStatus(event.status());
            claim.setUpdatedBy(event.updateBy());
            claim.setUpdatedAt(LocalDateTime.now());
            log.info("Inside approveClaimHandler, finished..");
        }
    }

    @Override
    @TransactionalEventListener
    public void changeClaimStatusHandler(ClaimEvent event) {
        Claim claim = findByRequest(event.requestId());
        if(claim != null) {
            claim.setStatus(event.status());
            claim.setUpdatedBy(event.updateBy());
            claim.setUpdatedAt(LocalDateTime.now());
        }
    }


    @Override
    public List<ClaimVO> findClaimsForSupplier(Long tenant, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return claimRepository.findClaimsForSupplier(tenant, page);
    }

    @Override
    public Claim findByRequest(Long requestId) {
        return claimRepository.findByRequest(requestId).orElse(null);
    }
}
