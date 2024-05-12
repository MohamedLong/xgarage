package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String partName;

    @Column(nullable = false, columnDefinition = "double default 0.0")
    private double price;
    private double discount = 0.0;
    private String discountType;
    private double vat = 0.0;
    private double originalPrice = 0.0;
    private int deliverDays = 1;
    private double qty = 1.0;
    @Column(nullable = false, columnDefinition = "double default 0.0")
    private double servicePrice;
    private int warranty;

    @ManyToOne
    @JoinColumn(name = "part_type_id", referencedColumnName = "id")
    private PartType partType;

    @Column(name = "order_id")
    private long order;

    @Column(nullable = false)
    @CreatedDate
    private Date bidDate;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Status status;

    @Version
    @Column(name = "version", columnDefinition = "bigint default 0")
    private long version;

    @ManyToOne
    @JoinColumn(name = "cu_id", referencedColumnName = "id", columnDefinition = "bigint default 1")
    private Currency cu;

    @Column(nullable = false, columnDefinition = "double default 0.0")
    private double cuRate;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Request request;

    @Column(name = "supplier_id")
    private long supplier;

    private long createUser;
    private long updateUser;
    
    private String comments;
    private String location;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bid_images", joinColumns = @JoinColumn(name = "bid_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "document_id", referencedColumnName = "id", unique = true))
    private List<Document> images =  new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "voice_note_id", referencedColumnName = "id")
    private Document voiceNote;

    @OneToOne
    @JoinColumn(name = "revise_voicenote_id", referencedColumnName = "id")
    private Document reviseVoiceNote;

    private String reviseComments;
    
    private String actionComments;

    @Transient
    private int operationCode = 0;
}
