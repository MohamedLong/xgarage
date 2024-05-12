package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
//    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    @OrderBy("name ASC")
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    public Category(String name){this.name = name;}
}
