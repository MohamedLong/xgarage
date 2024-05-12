package com.xgarage.app.model;

import com.xgarage.app.utils.RequestStatusConstants;
import genericlibrary.lib.generic.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.*;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Claim extends GenericEntity<Claim> {
    private String claimNo;
    private String claimTitle;
    private Date claimDate;
    @Column(name = "tenant_id")
    private Long tenant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    private String customerName;
    private String contactNo;
    private String excessRo;
    private String receivedBy;
    private Long inspectedBy;
    private Long surveyedBy;
    private Double repairCost;
    private Double repairHrs;
    private String officeLocation;

    private String workshopGrade;

    private Date bidClosingDate;

    @Enumerated(EnumType.STRING)
    private ClaimAssignType assignType;

    @Column(name = "garage_id")
    private Long assignedGarage;

    private String notes;
    private Date receivedDate;
    private Date excDeliveryDate;
    private Date breakDown;
    private Double km;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @OneToMany
    @JoinTable(name = "claim_documents",
            joinColumns = @JoinColumn(name = "claim_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "document_id", referencedColumnName = "id", unique = true))
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "claim")
    private List<ClaimSelectedTick> claimTicks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
//    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Status status = new Status(1L, "Open", "مفتوح");

    @ManyToOne
    @JoinColumn(name = "inspector_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    private Principle inspector;

    @ManyToOne
    @JoinColumn(name = "surveyer_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    private Principle surveyer;

    @ManyToMany
    @JoinTable(name = "claim_garages",
            joinColumns = @JoinColumn(name = "claim_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "garage_id", referencedColumnName = "id"))
    private List<Supplier> suppliers = new ArrayList<>();

    @Column(name = "request_id")
    private Long request;

    @Override
    public void update(Claim updatedClaim) {
        this.setClaimNo(updatedClaim.getClaimNo() == null ? this.getClaimNo() : updatedClaim.getClaimNo());
        this.setUpdatedAt(updatedClaim.getUpdatedAt());
        this.setUpdatedBy(updatedClaim.getUpdatedBy());
        this.setClaimDate(updatedClaim.getClaimDate() == null ? this.getClaimDate() : updatedClaim.getClaimDate());
        this.setTenant(updatedClaim.getTenant() == null ? this.getTenant() : updatedClaim.getTenant());
        this.setStatus(updatedClaim.getStatus() == null ? this.getStatus() : updatedClaim.getStatus());
        this.setAssignedGarage(updatedClaim.getAssignedGarage() == null ? this.getAssignedGarage() : updatedClaim.getAssignedGarage());
        this.setInspectedBy(updatedClaim.getInspectedBy() == null ? this.getInspectedBy() : updatedClaim.getInspectedBy());
        this.setSurveyedBy(updatedClaim.getSurveyedBy() == null ? this.getSurveyedBy() : updatedClaim.getSurveyedBy());
        this.setAssignType(updatedClaim.getAssignType() == null ? this.getAssignType() : updatedClaim.getAssignType());
        this.setBidClosingDate(updatedClaim.getBidClosingDate() == null ? this.getBidClosingDate() : updatedClaim.getBidClosingDate());
        this.setCustomerName(updatedClaim.getCustomerName() == null ? this.getCustomerName() : updatedClaim.getCustomerName());
        this.setContactNo(updatedClaim.getContactNo() == null ? this.getContactNo() : updatedClaim.getContactNo());
        this.setOfficeLocation(updatedClaim.getOfficeLocation() == null ? this.getOfficeLocation() : updatedClaim.getOfficeLocation());
        this.setExcessRo(updatedClaim.getExcessRo() == null ? this.getExcessRo() : updatedClaim.getExcessRo());
        this.setNotes(updatedClaim.getNotes() == null ? this.getNotes() : updatedClaim.getNotes());
        this.setRepairCost(updatedClaim.getRepairCost() == null ? this.getRepairCost() : updatedClaim.getRepairCost());
        this.setRepairHrs(updatedClaim.getRepairHrs() == null ? this.getRepairHrs() : updatedClaim.getRepairHrs());
        this.setWorkshopGrade(updatedClaim.getWorkshopGrade() == null ? this.getWorkshopGrade() : updatedClaim.getWorkshopGrade());
        this.setReceivedDate(updatedClaim.getReceivedDate() == null ? this.getReceivedDate() : updatedClaim.getReceivedDate());
        this.setKm(updatedClaim.getKm() == null ? this.getKm() : updatedClaim.getKm());
        this.setExcDeliveryDate(updatedClaim.getExcDeliveryDate() == null ? this.getExcDeliveryDate() : updatedClaim.getExcDeliveryDate());
        this.setBreakDown(updatedClaim.getBreakDown() == null ? this.getBreakDown() : updatedClaim.getBreakDown());
        this.setReceivedBy(updatedClaim.getReceivedBy() == null ? this.getReceivedBy() : updatedClaim.getReceivedBy());
    }

    public boolean changeStatus(Long updatedUser, Status newStatus) {
        if(!RequestStatusConstants.isAllowedStatusTransition(this.getStatus().getId(), newStatus.getId())) {
            return false;
        }
        this.setUpdatedBy(updatedUser);
        this.setStatus(newStatus);
        return true;
    }

    @Override
    public String toString() {
        return this.getClaimNo() + "/" + this.getClaimDate() + "/" + this.getStatus() + "/" + this.getTenant() + "/" + this.getCreatedBy() + "/" + this.getCreatedAt();
    }

}
