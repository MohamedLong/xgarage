package com.xgarage.app.repository;

import com.xgarage.app.dto.JobVO;
import com.xgarage.app.dto.JobsForSupplierVO;
import com.xgarage.app.dto.JobsForTenantVO;
import com.xgarage.app.dto.UpdateJobDto;
import com.xgarage.app.model.Job;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends GenericRepository<Job> {

    @Query(value = "select distinct job.id, job.job_no as jobNo, job.location as location, job.created_at as createdAt, " +
            " job.created_by as userId, job.status_id as status " +
            " from job, claim where job.claim_id = claim.id and claim.claim_no = :claimNo and job.tenant_id = :tenant", nativeQuery = true)
    List<JobVO> findByClaimNo(String claimNo, Long tenant);

//    @Query(value = "select distinct job.id, job.job_no as jobNo, claim.id as claimId, claim.claim_no as claimNo, job.insurance_type as insuranceType, job.created_at as createdAt, " +
//            " users.first_name as createdUser, car.chassis_number as carChassisNumber, car.gear_type as carGearType, car.plate_number as carPlateNumber, job.status as status, " +
//            " job.updated_at as statusDate from job, tenant, users, car, claim where job.created_by = users.id and job.car_id = car.id and " +
//            " job.claim_id = claim.id and claim.created_by = :userId", nativeQuery = true)
    List<Job> findByCreatedByOrderById(Long userId, Pageable pageable);

    boolean existsByJobNo(String jobNo);

    Optional<Job> findByJobNoAndCreatedBy(String jobNo, Long userId);

    @Query(value = "select distinct id, job_no as jobNo, (select count(1) from bid where request_id in(select id from request where job_id = job.id)) as submittedBids, job_title as jobTitle, status_id as status, (select name_en from status where id = job.status_id) as jobStatus, (select claim_no from claim where id = job.claim_id) as claimNo," +
            " (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, request r where p.id = r.part_id and r.job_id = job.id) as partNames from job where tenant_id = :tenant", nativeQuery = true)
    List<JobsForTenantVO> findJobsForTenant(Long tenant, Pageable pageable);

    @Query(value = "select distinct id, job_no as jobNo, (select count(1) from bid where request_id in(select id from request where job_id = job.id)) as submittedBids, job_title as jobTitle, status_id as status, (select name_en from status where id = job.status_id) as jobStatus, " +
            " (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, request r where p.id = r.part_id and r.job_id = job.id) as partNames, (select sum(price) from bid, request where bid.request_id = request.id and request.job_id = job.id and bid.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and bid.supplier_id = :tenant) as totalPrice, (select name from tenant where id = (select tenant_id from users where id = job.created_by)) as client from job where id in(select job_id from request where id in(select request_id from bid where supplier_id = :tenant))", nativeQuery = true)
    List<JobsForSupplierVO> findJobsForSupplier(Long tenant, Pageable pageable);

    @Query(value = "select distinct id, job_no as jobNo, (select count(1) from bid where request_id in(select id from request where job_id = job.id)) as submittedBids, job_title as jobTitle, status_id as status, (select name_en from status where id = job.status_id) as jobStatus, " +
            " (select GROUP_CONCAT(p.name SEPARATOR ', ') from part p, request r where p.id = r.part_id and r.job_id = job.id) as partNames, (select sum(price) from bid where supplier_id = :supplier)) as totalPrice, (select name from tenant where id = (select tenant_id from users where id = job.created_by)) as client from job where id in(select job_id from request where id in(select request_id from bid where supplier_id in(select id from users where tenant_id = :tenant)))", nativeQuery = true)
    List<JobsForSupplierVO> findJobsForSupplierInstance(Long supplier, Pageable pageable);

    @Query(value = "select distinct jobId as id, (select status_id from job where id = jobId) as status, (select name_en from status where id = (select status_id from job where id = jobId)) as jobStatus, (select job_no from job where id = jobId) as jobNo, (select claim_no from claim where id = (select claim_id from job where id = jobId)) as claimNo, (select job_title from job where id = jobId) as jobTitle, GROUP_CONCAT(partName SEPARATOR ', ') as partNames, sum(submittedBids) as submittedBids from (select distinct r.id as id, r.qty as qty, r.job_id as jobId, (select id from claim where id = r.job_id) as claimId, r.status_id as status, r.submission_date as submissionDate, r.user_id as userId, r.privacy as privacy, (select first_name from users where id = r.user_id) as firstName, r.request_title as requestTitle, (select name from part where id = r.part_id) as partName, (select count(*) from bid where request_id = r.id) as submittedBids, (select count(*) from bid where request_id = r.id and status_id in(select id from status where name_en in('Canceled', 'Rejected'))) as rejectedBids from request r where r.car_id in(select car.id from car where car.brand_id in(select supplier_brands.brand_id from supplier_brands where supplier_id = :tenant)) and r.status_id not in (select id from status where name_en in ('Canceled', 'Completed')) and r.id not in(select supplier_requests_notinterested.request_id from supplier_requests_notinterested where supplier_id = :tenant) and 1 = (case when r.privacy='Public' then 1 when r.privacy = 'Private' and r.id in (select rs.request_id from request_suppliers rs where rs.supplier_id = :tenant) then 1 end) and r.id in (select rr.id from request rr where rr.id not in(select rpt.request_id from request_part_types rpt) union (select rr.id from request rr where rr.id in(select rpt.request_id from request_part_types rpt where rpt.part_type_id in(select supplier_part_types.part_type_id from supplier_part_types where supplier_id = :tenant))))) as custom_query group by jobId, status having jobId is not null and jobStatus <> 'Open'", nativeQuery = true)
    List<JobsForTenantVO> findJobsRelatedToSupplier(Long tenant, Pageable pageable);

}
