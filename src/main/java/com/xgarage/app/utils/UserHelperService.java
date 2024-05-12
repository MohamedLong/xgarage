package com.xgarage.app.utils;

import com.xgarage.app.model.Supplier;
import com.xgarage.app.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public class UserHelperService {

    @Autowired private SupplierService supplierService;

    @Autowired
    HttpServletRequest request;

    public Long getAuthenticatedUser() {
        if(request.getHeader("id") != null) {
            return Long.valueOf(request.getHeader("id"));
        }
        return null;
    }

    public Supplier getAuthenticatedSupplier() {
        if(getAuthenticatedUser() != null) {
            return supplierService.findSupplierByUserId(getAuthenticatedUser());
        }
        return null;
    }

    public Long getTenant() {
        if(request.getHeader("tenant") != null) {
            return Long.valueOf(request.getHeader("tenant"));
        }
        return null;
    }

    public Long getTenantType() {
        if(request.getHeader("tenantType") != null) {
            return Long.valueOf(request.getHeader("tenantType"));
        }
        return null;
    }

    public Locale getLocaleFromUser() {
        try {
            return LocaleContextHolder.getLocale();
        }catch (Exception e) {
            return null;
        }
    }

    public Long getAuthenticatedSupplierId() {
        if(getAuthenticatedUser() != null) {
            return getTenant();
        }
        return null;
    }

    public String getUserToken() {
        if(request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}
