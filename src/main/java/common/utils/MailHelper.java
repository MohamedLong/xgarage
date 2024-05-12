package common.utils;

import common.event.SendMailEvent;
import common.model.GeneralMailLog;
import common.model.MailConfig;
import common.repository.GeneralMailLogRepo;
import common.repository.MailConfigRepository;
import ip.sms.smsapp.sms.dto.Email;
import ip.sms.smsapp.sms.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.function.Consumer;

@Component
@Slf4j
public class MailHelper {

    @Autowired
    private GeneralMailLogRepo mailLogRepo;

    @Autowired private EmailService emailService;

    @Autowired private MailConfigRepository mailConfigRepository;


    Consumer<SendMailEvent> sendMailListener(MailHelper mailHelper) {
        return this::sendMail;
    }
    @Async
    public void sendMail(SendMailEvent sendMailEvent){
        try {
            MailConfig mailConfig = mailConfigRepository.findAll().get(0);
            Email email = new Email();
            email.setBody(sendMailEvent.getBody());
            email.setFrom(mailConfig.getUser());
            email.setSubject(sendMailEvent.getSubject());
            email.setTo(sendMailEvent.getTo());
            email.setAttachment(sendMailEvent.getAttachment());
            emailService.sendSync(email);
        } catch (MessagingException e) {
            writeFailedMailIntoLog(sendMailEvent);
        }
    }

    private void writeFailedMailIntoLog(SendMailEvent mail) {
        GeneralMailLog mailLog = new GeneralMailLog();
        mailLog.setSubject(mail.getSubject());
        mailLog.setFrom(mail.getFrom());
        mailLog.setBody(mail.getBody());
        mailLog.setCreatedAt(new Date());
        mailLog.setService(mail.getService());
        mailLog.setServiceId(mail.getServiceId());
        mailLog.setTo(mail.getTo());
        mailLogRepo.save(mailLog);
    }
}
