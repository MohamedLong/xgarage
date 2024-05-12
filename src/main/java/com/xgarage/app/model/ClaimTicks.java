package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NaturalIdCache
@Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public class ClaimTicks extends GenericEntity<ClaimTicks> {

    @NaturalId
    private String name;

    @OneToMany(mappedBy = "tick")
    @JsonIgnore
    private List<ClaimSelectedTick> ticks = new ArrayList<>();
    @Override
    public void update(ClaimTicks genericEntity) {
        this.setUpdatedBy(genericEntity.getUpdatedBy());
        this.setName(genericEntity.getName());
    }
}
