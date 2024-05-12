package common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.dto.MessageResponse;
import ip.library.usermanagement.model.Document;
import ip.library.usermanagement.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/document")
@Slf4j
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = documentService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long documentId){
        try{
            Document document = documentService.findDocumentById(documentId);
            if(document == null) {
                return new ResponseEntity<>("Document Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(document);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error getting document", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveDocument(@RequestPart(value = "document", required = false) MultipartFile document){
        try{
            log.info("inside kernel service - document name: " + document.getSize());
            Document saveDocument = documentService.saveDocument(document);
            if(saveDocument != null) {
                return ResponseEntity.ok(saveDocument);
            }else {
                return ResponseEntity.badRequest().body("Unable to save Document");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error Saving Document");
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@RequestParam(value = "documentBody", required = false) String documentString, @RequestPart(value = "file", required = false) MultipartFile file){
        try{
            Document document = new ObjectMapper().readValue(documentString, Document.class);
            Document dbDocument = documentService.updateDocument(document, file);
            if(dbDocument != null) {
                return ResponseEntity.ok(dbDocument);
            }else {
                return ResponseEntity.badRequest().body("Unable to update Document");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error updating Document");
        }
    }

    @DeleteMapping("/delete/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable("docId") Long docId) {
        try{
            documentService.deleteDocumentById(docId);
            return ResponseEntity.ok().body(new MessageResponse("Success", HttpStatus.OK.value()));
        }catch(Exception e) {
            log.info("deleteRole Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Error Deleting Document", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

}
