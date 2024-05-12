package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false)
    private Long carModelId;

    @Column(nullable = false)
    private Long carModelYearId;

    @Column(nullable = false)
    private Long carModelTypeId;

    @Column(nullable = false)
    private String chassisNumber;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    private String plateNumber;

    @Enumerated(EnumType.STRING)
    private GearType gearType;
}
