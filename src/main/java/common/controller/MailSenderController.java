package common.controller;


import common.event.SendMailEvent;
import common.utils.MailHelper;
import ip.library.usermanagement.service.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/mail")
@Slf4j
public class MailSenderController {

    @Autowired private MailHelper mailHelper;
    @Autowired private UserHelper userHelper;
    @Autowired protected MessageSource messageSource;

    @PostMapping(value = "/sendMail")
    public ResponseEntity<?> sendMail(@RequestBody SendMailEvent sendMailEvent) {
        Locale locale = userHelper.getLocaleFromUser();
        try{
            mailHelper.sendMail(sendMailEvent);
            return ResponseEntity.ok("Success");
        }catch(Exception e) {
            log.error("Error inside SendMailController.sendMail: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getall.forbidden", null, locale), HttpStatus.FORBIDDEN);
        }
    }


}
