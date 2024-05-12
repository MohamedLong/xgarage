package com.xgarage.app.service;

import com.xgarage.app.dto.ClaimDto;
import com.xgarage.app.dto.JobsForSupplierVO;
import com.xgarage.app.dto.JobsForTenantVO;
import com.xgarage.app.dto.UpdateJobDto;
import com.xgarage.app.model.Job;
import com.xgarage.app.model.Status;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobService {
    ClaimDto findByClaimNo(String claimNo, Long tenant);

    Job findByIdCustom(Long id);

    @Transactional
    Job saveJob(Job job, MultipartFile carFile);

    @Transactional
    boolean partialUpdate(UpdateJobDto updateJobDto, Long updatedUser);

    List<Job> findByUser(Long userId, Integer pageNo, Integer pageSize);

    @Transactional
    boolean changeStatus(Long id, Long updatedUser, Status newStatus);

    boolean existsByJobNo(String jobNo);

    Job findByJobNoAndCreatedBy(String jobNo, Long userId);

    List<JobsForTenantVO> findJobsForTenant(Long tenant, Integer pageNo, Integer pageSize);

    List<JobsForSupplierVO> findJobsForSupplier(Long tenant, Integer pageNo, Integer pageSize);

    List<JobsForTenantVO> findJobsForRelatedToSupplier(Long tenant, Integer pageNo, Integer pageSize);
}
