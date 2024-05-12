package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier implements Serializable {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String cr;
    private String contactName;
    private String phoneNumber;
    private String email;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "supplier_locations",
            joinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false))
    Set<Location> locations;
    private String speciality;
    private String manufacturer;
    private String vehicleType;
    private int registeredYear;
    private Date registeredDate;
    @Column(nullable = false)
    private boolean enabled = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "supplier_service_types",
            joinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "serviceType_id", referencedColumnName = "id", nullable = false))
    private Set<ServiceType> serviceTypes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "supplier_part_types",
            joinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "partType_id", referencedColumnName = "id", nullable = false))
    private Set<PartType> partTypes;

    @Column(name = "user_id")
    private Long user;

    @Column(name = "tenant_id")
    private Long tenant;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "supplier_brands",
            joinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "brand_id", referencedColumnName = "id", nullable = false))
    private Set<Brand> brand;

    //Seller Info
    private int bestSeller = 0;
    private String region;
    private String status;
    private String bankName;
    private String branch;
    private String accountNo;
    private String holderName;
    private String image;
    private Double salesCommissionPercentage;

    @Transient
    private long submittedBids = 0;

    @Transient
    private long completedDeals = 0;

    @Transient
    private double rating = 0;

}
