package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brandName;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    @OneToMany
    @JoinColumn(name = "brand_id", referencedColumnName = "id", nullable = true)
    private List<CarModel> carModels = new ArrayList<>();

    public Brand(String brandName){
        this.brandName = brandName;
    }
}
