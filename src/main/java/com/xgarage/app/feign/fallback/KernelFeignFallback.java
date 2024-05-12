package com.xgarage.app.feign.fallback;

import com.xgarage.app.dto.Tenant;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class KernelFeignFallback implements KernelFeign {
    @Override
    public String changeUserRole(Long userId, String roleName) {
        throw new RuntimeException("Role not changed.");
    }

    @Override
    public void sendPushNotification(NotificationEvent notificationEvent) {

    }

    @Override
    public Document saveDocument(MultipartFile document) {
        throw new RuntimeException("Document not saved.");
    }

    @Override
    public Long getTenantAdmin(Long tenantId) {
        log.error("Inside KernelFallback Exception: ");
        return null;
    }

    @Override
    public Tenant getTenant(Long tenantId) {
        return null;
    }

    @Override
    public Tenant saveTenant(Tenant tenant) {
        return null;
    }
//
//    @Override
//    public Document updateDocument(Document document, MultipartFile file) {
//        throw new RuntimeException("Document not updated.");
//    }
//
//    @Override
//    public boolean deleteDocument(Long docId) {
//        return false;
//    }
//
//    @Override
//    public Document findDocumentById(Long docId) {
//        return null;
//    }
//
//    @Override
//    public Document getFile(String filename) {
//        return null;
//    }
}
