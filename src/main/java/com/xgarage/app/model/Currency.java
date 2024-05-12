package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cu;

    @Column(nullable = false, unique = true)
    private String cuName;

    @Column(nullable = false, columnDefinition = "double default 1.0")
    private Double cuRate;

}
