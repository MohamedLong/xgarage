package com.xgarage.app.model;

import com.xgarage.app.utils.RequestStatusConstants;
import genericlibrary.lib.generic.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Job extends GenericEntity<Job> {
    private String jobNo;
    private String jobTitle;

    @Column(name = "claim_id")
    private Long claim;

    @Transient
    private String claimNo;

    private String location;

    @Column(name = "tenant_id")
    private Long tenant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @ManyToMany
    @JoinTable(name = "job_suppliers",
            joinColumns = @JoinColumn(name = "job_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"))
    private List<Supplier> suppliers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
//    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Status status = new Status(1L, "Open", "مفتوح");

    @Override
    public void update(Job updatedJob) {
        this.setJobNo(updatedJob.getJobNo());
        this.setUpdatedAt(updatedJob.getUpdatedAt());
        this.setUpdatedBy(updatedJob.getUpdatedBy());
        this.setCar(updatedJob.getCar());
        this.setClaim(updatedJob.getClaim());
        this.setStatus(updatedJob.getStatus());
        this.setInsuranceType(updatedJob.getInsuranceType());
        this.setLocation(updatedJob.getLocation());
    }

    public boolean changeStatus(Long updatedUser, Status newStatus) {
        if(!RequestStatusConstants.isAllowedStatusTransition(this.getStatus().getId(), newStatus.getId())) {
            return false;
        }
        this.setUpdatedBy(updatedUser);
        this.setStatus(newStatus);
        return true;
    }

    public void convertFromClaim(Claim claim) {
        this.setClaimNo(claim.getClaimNo());
        this.setClaim(claim.getId());
        this.setJobTitle(claim.getClaimTitle());
        this.setCar(claim.getCar());
        this.setCreatedAt(LocalDateTime.now());
    }

}
