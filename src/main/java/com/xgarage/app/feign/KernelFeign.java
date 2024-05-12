package com.xgarage.app.feign;

import com.xgarage.app.dto.Tenant;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.fallback.KernelFeignFallback;
import com.xgarage.app.model.Document;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "xgarage-kernel-service", fallback = KernelFeignFallback.class)
public interface KernelFeign {
    @RequestMapping(method = RequestMethod.POST, value = "/api/changeUserRole/{userId}/{roleName}", produces = MediaType.APPLICATION_JSON_VALUE)
    String changeUserRole(@PathVariable Long userId, @PathVariable String roleName);
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/notifications/sendNotification", produces = MediaType.APPLICATION_JSON_VALUE)
    void sendPushNotification(@RequestBody NotificationEvent notificationEvent);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/document/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Document saveDocument(@RequestPart("document") MultipartFile document);

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/tenant/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Long getTenantAdmin(@PathVariable("tenantId") Long tenantId);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/tenant/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Tenant getTenant(@PathVariable("tenantId") Long tenantId);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/tenant/save", produces = MediaType.APPLICATION_JSON_VALUE)
    Tenant saveTenant(@RequestBody Tenant tenant);

//    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/document/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    Document updateDocument(@RequestBody Document document, @RequestParam("file") MultipartFile file);
//
//    @RequestMapping(method = RequestMethod.DELETE, value = "/api/v1/document/delete/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    boolean deleteDocumentById(@PathVariable Long docId);
//
//    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/document/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    Document findDocumentById(@PathVariable Long docId);
//
//    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/document/{filename}", produces = MediaType.APPLICATION_JSON_VALUE)
//    Document getFile(@PathVariable String filename);

}
