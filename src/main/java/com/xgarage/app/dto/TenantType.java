package com.xgarage.app.dto;

import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TenantType extends GenericEntity<TenantType> {
    private String name;

    @Override
    public void update(TenantType genericEntity) {

    }
}
