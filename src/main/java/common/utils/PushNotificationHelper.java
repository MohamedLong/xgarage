package common.utils;

import com.google.firebase.messaging.FirebaseMessagingException;
import common.event.NotificationEvent;
import common.event.SendMailEvent;
import ip.library.usermanagement.service.TenantService;
import ip.library.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.intelligentprojects.fcm.entity.Notification;
import net.intelligentprojects.fcm.entity.NotificationType;
import net.intelligentprojects.fcm.entity.PrincipleType;
import net.intelligentprojects.fcm.notification.PushNotificationRequest;
import net.intelligentprojects.fcm.notification.PushNotificationService;
import net.intelligentprojects.fcm.repository.NotificationRepository;
import net.intelligentprojects.fcm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PushNotificationHelper {
    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired private UserService userService;
    @Autowired private MailHelper mailHelper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TenantService tenantService;

    @Autowired private NotificationRepository notificationRepository;



    public void sendPushNotificationAsync(Map<String, String> data, PushNotificationRequest notificationRequest) {
        pushNotificationService.sendPushNotificationToTokenWithData(data, notificationRequest);
    }

    @Async
    public void sendPushNotification(String principleType, Long principleId, String type, Long typeId, String token, String keyTitle, String keyMessage, String topic) {
        try{
            Map<String,String> data = new HashMap<>();
            data.put("type", type);
            data.put("typeId", String.valueOf(typeId));
            saveNotification(keyTitle, keyMessage, type, typeId, principleType, principleId);
            sendPushNotificationAsync(data, new PushNotificationRequest(keyTitle, keyMessage, topic, token));
        }catch(Exception e) {
            log.error("PushNotification Exception: " + e.getMessage());
        }
    }


    @Bean
    Consumer<NotificationEvent> pushNotificationLister(PushNotificationHelper notificationHelper){
        return notificationHelper::sendMultiCastNotification;
    }

    @Async
    public void sendMultiCastNotification(NotificationEvent event) {
        try {
            persistNotificationEvent(event);
            if(TenantTypeConstants.Public.equals(event.getTenantType())) {
                sendNotificationViaMobile(event);
            }else{
                sendNotificationViaEmail(event);
            }
        } catch (Exception e) {
            log.error("[EXCEPTION] handlePushNotificationEvent:{},{},{}",e.getMessage(),e.getCause(),e);
        }
    }

    public void sendNotificationViaMobile(NotificationEvent event) throws ExecutionException, FirebaseMessagingException, InterruptedException {
        Map<String, String> data = new HashMap<>();
        data.put("type", event.getType());
        data.put("typeId", String.valueOf(event.getTypeId()));
        List<String> tokens = userService.findUserTokensByIds(event.getPrincipleIds());
        if(tokens == null || tokens.isEmpty()){
            log.error("Tokens not found");
            return;
        }
        pushNotificationService.sendMulticastMessageToTokensWithData(data, tokens, buildNotificationRequest(event));
    }

    public void sendNotificationViaEmail(NotificationEvent event)  {
        List<Long> userList = event.getPrincipleIds();
        List<String> emails = tenantService.findTenantEmailsByUserIdList(userList);
        HashMap<Long, String> userMails = new HashMap<>();
        for(int i = 0;i < emails.size(); i++) {
            userMails.put(userList.get(i), emails.get(i));
        }
        if(emails.isEmpty()){
            log.error("Emails not found");
            return;
        }
        userList.parallelStream().forEach(u -> {
            SendMailEvent sendMailEvent = SendMailEvent
                    .builder()
                    .to(userMails.get(u))
                    .subject(event.getTitle())
                    .body(event.getMessage())
                    .service(event.getType())
                    .serviceId(event.getTypeId())
                    .build();
            mailHelper.sendMail(sendMailEvent);
        });
    }

    public void persistNotificationEvent(NotificationEvent event){
        List<Notification> notificationList  = createFromEvent(event);
        notificationRepository.saveAll(notificationList);
    }

    public static List<Notification> createFromEvent(NotificationEvent event){
        return event.getPrincipleIds().stream().map(principleId ->
                Notification
                        .builder()
                        .principleId(principleId)
                        .principleType(PrincipleType.valueOf(event.getPrincipleType()))
                        .type(NotificationType.valueOf(event.getType()))
                        .typeId(event.getTypeId()).title(event.getTitle())
                        .description(event.getMessage())
                        .createdAt(new Date(System.currentTimeMillis()))
                        .build())
                .collect(Collectors.toList());
    }

    public PushNotificationRequest buildNotificationRequest(NotificationEvent event) {
        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
        pushNotificationRequest.setMessage(event.getMessage());
        pushNotificationRequest.setTitle(event.getTitle());
        pushNotificationRequest.setTopic(event.getTopic());
        return pushNotificationRequest;
    }


    @Transactional
    public void saveNotification(String keyTitle, String keyMessage, String type, Long typeId, String principleType, Long principleId) {
        Notification notification = new Notification();
        notification.setTitle(keyTitle);
        notification.setDescription(keyMessage);
        notification.setStatus(true);
        notification.setPrincipleType(PrincipleType.valueOf(principleType));
        notification.setPrincipleId(principleId);
        notification.setType(NotificationType.valueOf(type));
        notification.setTypeId(typeId);
        Date currentDate = new Date(System.currentTimeMillis());
        notification.setCreatedAt(currentDate);
        notificationService.create(notification);
    }



}