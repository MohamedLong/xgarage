package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "subCategory")
//    @JoinColumn(name = "subcategory_id", referencedColumnName = "id")
    @Where(clause = "status = 0")
    @OrderBy("name ASC")
    private List<Part> parts = new ArrayList<>();

    @JoinColumn(name = "category_id")
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Category category;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    public SubCategory(String name){this.name = name;}
}
