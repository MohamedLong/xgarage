package com.xgarage.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import genericlibrary.lib.generic.GenericEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tenant extends GenericEntity<Tenant> {
    private String name;
    private String cr;
    private String location;
    private String email;
    private boolean enabled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_type", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private TenantType tenantType;
    @Override
    public void update(Tenant genericEntity) {

    }
}
