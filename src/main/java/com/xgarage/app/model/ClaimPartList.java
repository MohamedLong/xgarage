package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimPartList extends GenericEntity<ClaimPartList> {
    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Part part;

    @Override
    public void update(ClaimPartList genericEntity) {

    }
}
