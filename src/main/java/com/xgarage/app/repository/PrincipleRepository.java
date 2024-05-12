package com.xgarage.app.repository;

import com.xgarage.app.model.Principle;
import genericlibrary.lib.generic.GenericRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrincipleRepository extends GenericRepository<Principle> {
    List<Principle> findByTenant(Long tenantId);
}
