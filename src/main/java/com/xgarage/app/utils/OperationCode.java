package com.xgarage.app.utils;

import com.xgarage.app.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class OperationCode {
    public static final Integer SUCCESS_CODE = 0;
    public static final Integer GENERAL_ERROR_CODE = -1;
    public static final Integer OUT_OF_ALLOWED_BIDS_CODE = -2;

    public static final Integer Cancelled_Request = -3;
    public static final Integer Completed_Request = -4;
    protected static final Locale locale = LocaleContextHolder.getLocale();


    @Autowired private MessageSource messageSource;
    

    public ResponseEntity<?> craftResponse(String messageKey, HttpStatus status) {
        return new ResponseEntity<>(new MessageResponse(messageSource.getMessage(messageKey,null, locale), status.value()), status);
    }
}
