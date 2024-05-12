package common.config;

import common.feign.fallback.CoreFeignFallback;
import common.model.MailConfig;
import common.repository.MailConfigRepository;
import common.utils.TenantTypeConstants;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import ip.sms.smsapp.sms.config.EmailConfig;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;

@Configuration
@Slf4j
public class GeneralConfig {

    public HashMap<Long, String> tenantRoles = new HashMap<>();
    public final static String DEFAULT_CIRCUIT_BREAKER_REGISTRY = "DEFAULT_CIRCUIT_BREAKER_REGISTRY";

    @Bean
    public HashMap<Long, String> loadDefaultTenantRoles() {
        tenantRoles.put(TenantTypeConstants.Garage, "ROLE_GARAGE_USER");
        tenantRoles.put(TenantTypeConstants.Supplier, "ROLE_SUPPLIER_USER");
        tenantRoles.put(TenantTypeConstants.Insurance, "ROLE_INSURANCE_USER");
        tenantRoles.put(TenantTypeConstants.XGarage, "ROLE_XGARAGE_USER");
        return tenantRoles;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Autowired private MailConfigRepository mailConfigRepository;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public JavaMailSender javaMailSender(){
        EmailConfig emailConfig = new EmailConfig();
        MailConfig mailConfig = mailConfigRepository.findAll().get(0);

        emailConfig.setDebug(mailConfig.isDebugMode());
        emailConfig.setPort(mailConfig.getPort());
        emailConfig.setProtocol("smtp");
        emailConfig.setHost(mailConfig.getHost());
        emailConfig.setUser(mailConfig.getUser());
        emailConfig.setPassword(mailConfig.getPassword());
        emailConfig.setAuth("true");
        emailConfig.setSsl("true");
        return emailConfig.getJavaMailSender();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
