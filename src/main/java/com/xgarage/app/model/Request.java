package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xgarage.app.dto.BidDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    
    @CreatedDate
    private Timestamp bidClosingDate;
    @Enumerated(EnumType.STRING)
    private Privacy privacy;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Status status = new Status(1L, "Open", "مفتوح");
    
    @CreatedDate
    private Timestamp submissionDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Car car;

    @Enumerated(EnumType.STRING)
    private RequestKind requestType = RequestKind.Part;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document voiceNote;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id", nullable = true)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Part part;

    private Double qty = 1.0;

    @OneToMany
    @JoinTable(name = "request_documents",
            joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "document_id", referencedColumnName = "id", unique = true))
    private List<Document> documents = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "request_part_types",
            joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "part_type_id", referencedColumnName = "id"))
    private List<PartType> partTypes = new ArrayList<>();
    
    @Column(name = "tenant_id")
    private Long tenant;

    @Column(name = "job_id")
    private Long job;
    
    @ManyToMany
    @JoinTable(name = "request_suppliers",
            joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"))
    private List<Supplier> suppliers = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "supplier_requests_notinterested",
            joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"))
    private Set<Supplier> notInterestedSuppliers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "request_questions",
            joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"))
    private List<Mcq> questions;

    @Column(name = "user_id", nullable = false)
    private Long user;

    @Version
    @Column(name = "version", columnDefinition = "bigint default 0")
    private Long version;
    private Long updateUserId;
    private String requestTitle;
    private String locationName;
    
    @Transient
    private long submittedBids = 0;
    @Transient
    private long rejectedBids = 0;
    @Transient
    private BidDto selectedBid = null;

}
