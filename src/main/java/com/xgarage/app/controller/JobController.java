package com.xgarage.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgarage.app.dto.*;
import com.xgarage.app.model.*;
import com.xgarage.app.service.serviceimpl.JobServiceImpl;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.TenantTypeConstants;
import com.xgarage.app.utils.UserHelperService;
import genericlibrary.lib.generic.GenericController;
import genericlibrary.lib.generic.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/core/api/v1/job")
@Slf4j
public class JobController extends GenericController<Job> {

    @Autowired private JobServiceImpl jobService;

    @Autowired private UserHelperService userHelper;

    @Autowired private OperationCode operationCode;

    public JobController(GenericService<Job> service) {
        super(service);
    }

    @GetMapping("/claimNo/{cno}")
    public ResponseEntity<?> findByClaimNo(@PathVariable("cno") String claimNo) {
        Long tenant = userHelper.getTenant();
        try{
            ClaimDto claim = jobService.findByClaimNo(claimNo, tenant);
            if(claim == null) {
                return operationCode.craftResponse("operation.claim.notfound",  HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(claim);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.claim.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try{
            Job fetchedJob = jobService.findByIdCustom(id);
            if(fetchedJob == null) {
                return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(fetchedJob);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/jobNo/{jobNo}")
    public ResponseEntity<?> findByJobNo(@PathVariable("jobNo") String jobNo) {
        Long userId = userHelper.getAuthenticatedUser();
        try{
            Job fetchedJob = jobService.findByJobNoAndCreatedBy(jobNo, userId);
            if(fetchedJob == null) {
                return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(fetchedJob);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> findByUser(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        try{
            Long userId = userHelper.getAuthenticatedUser();
            if(userId != null) {
                List<Job> jobs = jobService.findByUser(userId, pageNo, pageSize);
                if(jobs == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(jobs);
            }
            return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/brandOrPartNameLike/{name}")
    public ResponseEntity<?> findJobsByBrandOrPartNameLike(@PathVariable("name") String name) {
        try{
            Long tenant = userHelper.getTenant();
            List<JobsForTenantVO> jobs = jobService.findJobsForRelatedToSupplier(tenant, 0, 100);
            if(jobs != null) {
                List<JobsForTenantVO> filteredJobs = jobs
                        .stream()
                        .filter(job -> job.getJobTitle() != null && job.getPartNames() != null)
                        .toList()
                        .stream()
                        .filter(j -> j.getJobTitle().toLowerCase().contains(name.toLowerCase()) || j.getPartNames().toLowerCase().contains(name.toLowerCase()))
                        .toList();
                if(filteredJobs != null) {
                    return ResponseEntity.ok().body(filteredJobs);
                }
                return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
            }
            return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/tenant")
    public ResponseEntity<?> findByTenant(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        try{
            Long tenant = userHelper.getTenant();
            Long tenantType = userHelper.getTenantType();
            if(tenant != null && TenantTypeConstants.Supplier.equals(tenantType) ) {
                List<JobsForTenantVO> jobs = jobService.findJobsForRelatedToSupplier(tenant, pageNo, pageSize);
                if(jobs == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(jobs);
            }
            if (tenant != null) {
                List<JobsForTenantVO> jobs = jobService.findJobsForTenant(tenant, pageNo, pageSize);
                if(jobs == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(jobs);
            }
            return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/tenantSupplier")
    public ResponseEntity<?> findBySupplierTenant(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        try{
            Long tenant = userHelper.getTenant();
            if (tenant != null) {
                List<JobsForSupplierVO> jobs = jobService.findJobsForSupplier(tenant, pageNo, pageSize);
                if(jobs == null) {
                    return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok().body(jobs);
            }
            return operationCode.craftResponse("operation.job.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<?> create(@RequestBody Job created, @RequestHeader Map<String, String> headers) {
        try{
            Long tenant = userHelper.getTenant();
            Long userId = userHelper.getAuthenticatedUser();
            if (userId != null) {
                created.setCreatedBy(userId);
            }
            if(created.getTenant() == null) {
                created.setTenant(tenant);
            }
            Job savedJob = jobService.saveJob(created, null);
            if(savedJob == null) {
                return operationCode.craftResponse("operation.job.save.badrequest", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(savedJob);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.save.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("updateJob")
    public ResponseEntity<?> updateJob(@RequestBody UpdateJobDto jobDto) {
        try{
            Long updatedUser = userHelper.getAuthenticatedUser();
            if(jobService.partialUpdate(jobDto, updatedUser)) {
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.save.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/saveJob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveJob(@RequestParam(value = "jobBody", required = false) String stringJob, @RequestParam(value = "carDocument", required = false) Optional<MultipartFile> carFile) {
        try{
            Long tenant = userHelper.getTenant();
            Long userId = userHelper.getAuthenticatedUser();
            Job job = new ObjectMapper().readValue(stringJob, Job.class);
            if(jobService.existsByJobNo(job.getJobNo())) {
                operationCode.craftResponse("operation.job.found", HttpStatus.FOUND);
            }
            if (userId != null) {
                job.setCreatedBy(userId);
            }
            if(job.getTenant() == null) {
                job.setTenant(tenant);
            }
            Job savedJob = jobService.saveJob(job, carFile.orElse(null));
            if(savedJob == null) {
                return operationCode.craftResponse("operation.job.save.badrequest", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(savedJob.getId());
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.job.save.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable("id") Long jobId, @RequestBody Status status){
        try{
            Long updatedUser = userHelper.getAuthenticatedUser();
            if(jobService.changeStatus(jobId, updatedUser, status)) {
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);

        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.forbidden", HttpStatus.FORBIDDEN);
        }
    }

}
