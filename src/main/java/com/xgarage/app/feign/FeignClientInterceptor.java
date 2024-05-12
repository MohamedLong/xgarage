package com.xgarage.app.feign;

import com.xgarage.app.utils.UserHelperService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired private UserHelperService userHelperService;


    @Override
    public void apply(RequestTemplate requestTemplate) {

        requestTemplate.header(HttpHeaders.AUTHORIZATION, userHelperService.getUserToken());
        requestTemplate.header("id", userHelperService.getAuthenticatedUser().toString());
        requestTemplate.header("tenant", userHelperService.getTenant().toString());
        requestTemplate.header("tenantType", userHelperService.getTenantType().toString());
    }
}
