package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimBid extends GenericEntity<ClaimBid> {

    @Column(name = "bid_id")
    private Long bid;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Part part;

    @Column(name = "part_type_id")
    private Long partType;

    @Enumerated(EnumType.STRING)
    private PartOption requestFor;

    @Enumerated(EnumType.STRING)
    private PartOption partOption;

    private Double qty = 1.0;
    @Column(nullable = false, columnDefinition = "double default 0.0")
    private Double price;
    @Column(nullable = false, columnDefinition = "double default 0.0")
    private Double servicePrice;
    private Double discount = 0.0;
    private String discountType;
    private Double vat = 0.0;
    private Double originalPrice = 0.0;
    private int warranty;
    private int availability;

    @Override
    public void update(ClaimBid genericEntity) {
        this.setBid(genericEntity.getBid());
        this.setDiscount(genericEntity.getDiscount());
        this.setAvailability(genericEntity.getAvailability());
        this.setPart(genericEntity.getPart());
        this.setQty(genericEntity.getQty());
        this.setPrice(genericEntity.getPrice());
        this.setVat(genericEntity.getVat());
        this.setDiscountType(genericEntity.getDiscountType());
        this.setOriginalPrice(genericEntity.getOriginalPrice());
        this.setPartOption(genericEntity.getPartOption());
        this.setPartType(genericEntity.getPartType());
        this.setRequestFor(genericEntity.getRequestFor());
        this.setServicePrice(genericEntity.getServicePrice());
        this.setAvailability(genericEntity.getAvailability());
    }
}
