package com.xgarage.app.service.serviceimpl;


import com.xgarage.app.model.Document;
import com.xgarage.app.repository.DocumentRepository;
import com.xgarage.app.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final String storePath;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, @Value("${file-store}") String storePath){
        this.documentRepository = documentRepository;
        this.storePath = storePath;
    }

    @PostConstruct
    public void init() {
        try {
            Path rootPath = Paths.get(storePath);
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }
    @Override
    public Document findProxyDocumentById(Long id){return documentRepository.getReferenceById(id);}

    @Override
    public Document findDocumentById(Long id){
        Optional<Document> documentOptional = documentRepository.findById(id);
        return documentOptional.orElse(null);
    }

    @Override
    public List<Document> findAllDocuments(){return documentRepository.findAll();}

    @Override
    public Page<Document> findDocumentPage(Pageable pageable){return documentRepository.findAll(pageable);}

    @Override
    public Document saveDocument(MultipartFile file){
        try {
            Document document = new Document();
            String name = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + name;
            String extension = name.substring(name.lastIndexOf("."));
            document.setName(fileName);
            document.setExtension(extension);
            String imagePath = storePath + File.separator + fileName;
            File tf = new File(imagePath);
            if (!tf.exists()) {
                System.out.println("*********** "+tf.getPath()+" ***********");
                Files.copy(file.getInputStream(), Paths.get(tf.getPath()));
            }else{
                throw new FileAlreadyExistsException("File " + name + " Already Exists");
            }
            return documentRepository.save(document);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Document updateDocument(Document doc, MultipartFile file){
        try {
            if(doc != null && doc.getId() != null && documentRepository.existsById(doc.getId())){
                if(file == null)
                    return null;
                String name = file.getOriginalFilename();
                String fileName = UUID.randomUUID() + name;
                String extension = name.substring(name.lastIndexOf("."));
                Document document = new Document();
                document.setId(doc.getId());
                document.setName(fileName);
                document.setExtension(extension);
                String imagePath = storePath + File.separator + fileName;
                File tf = new File(imagePath);
                if (!tf.exists()) {
                    Files.copy(file.getInputStream(), Paths.get(tf.getPath()));
                    Path deleted = Paths.get(storePath)
                            .resolve(storePath + File.separator + document.getName());
                    Resource resource = new UrlResource(deleted.toUri());
                    resource.getFile().delete();
                }else{
                    throw new FileAlreadyExistsException("File " + name + " Already Exists");
                }
                return documentRepository.save(document);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteDocumentById(Long id){
        try{
            if(documentRepository.existsById(id)) {
                Document document = findDocumentById(id);
                Path file = Paths.get(storePath)
                        .resolve(storePath + File.separator + document.getName());
                Resource resource = new UrlResource(file.toUri());
                resource.getFile().delete();
                documentRepository.delete(document);
                return true;
            }else {
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = Paths.get(storePath)
                    .resolve(storePath + File.separator + filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
