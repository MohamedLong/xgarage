package com.xgarage.app.model;

import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Principle extends GenericEntity<Principle> {

    private String name;
    private Long tenant;
    private String role;

    @Override
    public void update(Principle genericEntity) {
        this.setName(genericEntity.getName());
        this.setTenant(genericEntity.getTenant());
    }
}
