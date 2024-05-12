package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "int default 1")
    private int status;

    @JoinColumn(name = "subcategory_id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private SubCategory subCategory;

    @Transient
    private Long subCategoryId;

    @Transient
    private Long categoryId;

    public Long getSubCategoryId() {
        return subCategory.getId();
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Long getCategoryId() {
        return subCategory.getCategory().getId();
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Part(String name){this.name = name;}

    public Part update(Part newPart) {
        this.setName(newPart.getName());
        this.setStatus(newPart.getStatus());
        return this;
    }


}
