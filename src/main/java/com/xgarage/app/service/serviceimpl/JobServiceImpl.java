package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.dto.*;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.*;
import com.xgarage.app.repository.JobRepository;
import com.xgarage.app.service.*;
import com.xgarage.app.utils.RequestStatusConstants;
import genericlibrary.lib.generic.GenericRepository;
import genericlibrary.lib.generic.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobServiceImpl extends GenericService<Job> implements JobService {

    @Autowired private JobRepository jobRepository;
    @Autowired private ClaimServiceImpl claimService;

    @Autowired private ClaimPartsService claimPartsService;

    @Autowired private CarService carService;

    @Autowired private SupplierService supplierService;

    @Autowired private KernelFeign kernelFeign;

    @Autowired private RequestService requestService;

    @Value("${redirect.mail.address}")
    private String redirectMailAddress;


    public JobServiceImpl(GenericRepository<Job> repository) {
        super(repository);
    }

    @Override
    public ClaimDto findByClaimNo(String claimNo, Long tenant) {
        Claim claim = claimService.findByClaimNo(claimNo);
        if(claim != null) {
            List<JobVO> jobs = jobRepository.findByClaimNo(claimNo, tenant);
            return new ClaimDto(claim.getId(), claimNo, jobs);
        }
        return null;
    }

    @Override
    public Job findByIdCustom(Long id) {
        Job fetchedJob = this.get(id);
        if(fetchedJob != null) {
            Claim claim = claimService.get(fetchedJob.getClaim());
            fetchedJob.setClaimNo(claim.getClaimNo());
            return fetchedJob;
        }
        return null;
    }

    @Override
    @Transactional
    public Job saveJob(Job job, MultipartFile carFile) {
        try{
            if(job.getCar() != null) {
                Car dbCar;
                if(job.getCar().getId() != null) {
                    dbCar = carService.findCarById(job.getCar().getId());
                }else if(carFile != null) {
                    dbCar = carService.saveFullCar(job.getCar(), carFile);
                }else{
                    dbCar = carService.saveCar(job.getCar());
                }
                if(dbCar != null) {
                    job.setCar(dbCar);
                }
            }
            if(job.getPrivacy() == null) {
                job.setPrivacy(Privacy.Public);
            }

            if(job.getSuppliers() != null && job.getSuppliers().size() > 0){
                job.setSuppliers(job.getSuppliers().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
            }
            return jobRepository.save(job);
        }catch(Exception e) {
            log.info("Error inside JobService.saveJob: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean partialUpdate(UpdateJobDto updateJobDto, Long updatedUser) {
        try{
            Job job = get(updateJobDto.id());
            if(job != null) {
                if(updateJobDto.jobNumber() != null) {
                    job.setJobNo(updateJobDto.jobNumber());
                }
                job.setUpdatedBy(updatedUser);
                if(updateJobDto.privacy() != null) {
                    job.setPrivacy(updateJobDto.privacy());
                    if(Privacy.Private.equals(updateJobDto.privacy()) && updateJobDto.supplierList() != null && !updateJobDto.supplierList().isEmpty()) {
                        job.setSuppliers(updateJobDto.supplierList().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
                        requestService.findAllJobRequests(0, 100, job.getId()).forEach(request -> {
                            request.setUpdateUserId(updatedUser);
                            request.setPrivacy(updateJobDto.privacy());
                            request.setSuppliers(updateJobDto.supplierList().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
                        });
                    }
                }
                return true;
            }
            return false;
        }catch(Exception e) {
            log.error("Exception inside JobService.partialUpdate: " + e.getMessage());
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }



    @Override
    public List<Job> findByUser(Long userId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return jobRepository.findByCreatedByOrderById(userId, page);
    }

    @Override
    @Transactional
    public boolean changeStatus(Long id, Long updatedUser, Status newStatus) {
        Job fetchedJob = jobRepository.getReferenceById(id);
        boolean changed = fetchedJob.changeStatus(updatedUser, newStatus);
        log.info("status: " + changed);
        if(changed) {
            notify(id, newStatus, fetchedJob);
        }
        return changed;
    }

    private void notify(Long id, Status newStatus, Job fetchedJob) {
        if(RequestStatusConstants.CONFIRMED_STATUS == newStatus.getId()) {
            String keyJobApprovedTitle = "New Request for Job Number: " + fetchedJob.getJobNo();
            StringBuilder body = new StringBuilder(1000);
            String keyJobApprovedMessage = "New Request with ID #" + id + " Has Been Submitted.\n";
            body.append(keyJobApprovedMessage);
            body.append("Please click on the following link to go to the request details in X Garage:\n");
            body.append(redirectMailAddress + "#/job-details?id=" + id);
            sendPushNotification(fetchedJob, keyJobApprovedTitle, body.toString());
        }else if(RequestStatusConstants.CANCELED_STATUS == newStatus.getId()) {
            String keyJobCanceledTitle = "Cancel Request for Job Number: " + fetchedJob.getJobNo();
            String keyJobCanceledMessage = "Request with ID #" + id + " Has Been Canceled.";
            sendPushNotification(fetchedJob, keyJobCanceledTitle, keyJobCanceledMessage);
        }
    }

    public void sendPushNotification(Job job, String keyTitle, String keyMessage) {
        try {
            NotificationEvent multiNotificationEvent = null;
            if (Privacy.Public.equals(job.getPrivacy())) {
                List<Long> supplierUsers = supplierService.findSuppliersUserIdListByBrand(job.getCar().getBrandId());
                if (supplierUsers != null) {
                    multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), supplierUsers, NotificationType.Request.name(), job.getId(), keyTitle, keyMessage, "Public");
                }
            } else {
                multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), job.getSuppliers().parallelStream().map(Supplier::getUser).collect(Collectors.toList()), NotificationType.Request.name(), job.getId(), keyTitle, keyMessage, "Private");
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
    public boolean existsByJobNo(String jobNo) {
        return jobRepository.existsByJobNo(jobNo);
    }

    @Override
    public Job findByJobNoAndCreatedBy(String jobNo, Long userId) {
        return jobRepository.findByJobNoAndCreatedBy(jobNo, userId).orElse(null);
    }

    @Override
    public List<JobsForTenantVO> findJobsForTenant(Long tenant, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return jobRepository.findJobsForTenant(tenant, page);
    }
    @Override
    public List<JobsForSupplierVO> findJobsForSupplier(Long tenant, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return jobRepository.findJobsForSupplier(tenant, page);
    }

    @Override
    public List<JobsForTenantVO> findJobsForRelatedToSupplier(Long tenant, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return jobRepository.findJobsRelatedToSupplier(tenant, page);
    }
}
