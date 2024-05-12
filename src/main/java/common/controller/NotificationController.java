package common.controller;

import common.event.NotificationEvent;
import common.feign.CoreFeign;
import common.utils.PushNotificationHelper;
import ip.library.usermanagement.model.User;
import ip.library.usermanagement.service.UserHelper;
import lombok.extern.slf4j.Slf4j;
import net.intelligentprojects.fcm.dto.NotificationVO;
import net.intelligentprojects.fcm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin
@Slf4j
public class NotificationController {


    @Autowired private NotificationService notificationService;

    @Autowired private UserHelper userHelper;

    @Autowired private CoreFeign coreFeign;

    @Autowired protected MessageSource messageSource;

    @Autowired private PushNotificationHelper notificationHelper;


    @GetMapping("/user")
    public ResponseEntity<?> findAllSellerNotifications() {
        Locale locale = userHelper.getLocaleFromUser();
        User user = userHelper.getAuthenticatedUser();
        try{
            List<NotificationVO> notifications = notificationService.findAllUserNotifications(user.getId());
            if(notifications.isEmpty()) {
                return new ResponseEntity<>(messageSource.getMessage("getall.notfound", null, locale), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(notifications);
        }catch(Exception e) {
            log.error("Error inside WishlistController.findAllWishlistProductsByUser: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/seller")
    public ResponseEntity<?> findAllUserNotifications() {
        Locale locale = userHelper.getLocaleFromUser();
        Long supplierId = coreFeign.getSupplierId(userHelper.getAuthUserId());
        try{
            List<NotificationVO> notifications = notificationService.findAllSupplierNotifications(supplierId);
            if(notifications.isEmpty()) {
                return new ResponseEntity<>(messageSource.getMessage("getall.notfound", null, locale), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(notifications);
        }catch(Exception e) {
            log.error("Error inside WishlistController.findAllWishlistProductsByUser: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/sendNotification")
    public ResponseEntity<?> sendPushNotification(@RequestBody NotificationEvent notificationEvent, HttpServletRequest request) {
        Locale locale = userHelper.getLocaleFromUser();
        try{
            notificationEvent.setTenantType(Long.valueOf(request.getAttribute("tenantType").toString()));
            notificationHelper.sendMultiCastNotification(notificationEvent);
            return ResponseEntity.ok("Success");
        }catch(Exception e) {
            log.error("Error inside NotificationController.sendPushNotification: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }


}
