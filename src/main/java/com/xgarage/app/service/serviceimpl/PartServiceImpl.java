package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.PartRepository;
import com.xgarage.app.model.Part;
import com.xgarage.app.service.PartService;
import com.xgarage.app.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartServiceImpl implements PartService {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    SubCategoryService subCategoryService;

    @Override
    public Part findProxyPartById(Long id){return partRepository.getReferenceById(id);}

    @Override
    public Part findPartById(Long id){
        Optional<Part> partOptional = partRepository.findById(id);
        return partOptional.orElse(null);
    }

    @Override
    public List<Part> getPartByNameLike(String partName) {
        return partRepository.findByNameContainingIgnoreCaseOrderByName(partName);
    }

    @Override
    public List<Part> findAllParts(){return partRepository.findAll();}

    @Override
    public Page<Part> findPartPage(Pageable pageable){return partRepository.findAll(pageable);}

    @Override
    public Part savePart(Part part){
        if(part.getSubCategory() != null) {
            part.setSubCategory(subCategoryService.findSubCategoryById(part.getSubCategory().getId()));
        }
        return partRepository.save(part);
    }

    @Override
    public boolean deletePartById(Long id){
        try {
            partRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Part getPartById(Long partId) {
        return partRepository.findById(partId).orElse(null);
    }

    @Override
    @Transactional
    public Part updatePart(Part newPart) {
        return partRepository.findById(newPart.getId()).get().update(newPart);
    }

//    public Part addDocumentToPart(Long documentId, Long partId){
//        try {
//            Document document = kernelFeign.findProxyDocumentById(documentId);
//            Part part = findProxyPartById(partId);
//            part.getDocuments().add(document);
//            return part;
//        }catch (Exception ex){
//            return null;
//        }
//    }
}
