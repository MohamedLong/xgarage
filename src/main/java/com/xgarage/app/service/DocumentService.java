package com.xgarage.app.service;

import com.xgarage.app.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    Document findProxyDocumentById(Long id);

    Document findDocumentById(Long id);

    List<Document> findAllDocuments();

    Page<Document> findDocumentPage(Pageable pageable);

    Document saveDocument(MultipartFile file);

    Document updateDocument(Document doc, MultipartFile file);

    boolean deleteDocumentById(Long id);

    Resource load(String filename);
}
