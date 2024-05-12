package com.xgarage.app.service;

import com.xgarage.app.dto.PartDto;
import com.xgarage.app.model.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface PartService {
    Part findProxyPartById(Long id);

    Part findPartById(Long id);

    List<Part> getPartByNameLike(String partName);

    List<Part> findAllParts();

    Page<Part> findPartPage(Pageable pageable);

    Part savePart(Part part);

    boolean deletePartById(Long id);

    Part getPartById(Long partId);

    Part updatePart(Part part);
}
