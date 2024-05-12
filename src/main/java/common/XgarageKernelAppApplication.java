package common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;


@SpringBootApplication
@Slf4j
@EnableTransactionManagement
//@EnableCaching
@EnableFeignClients
@EntityScan(basePackages = {"common.model","ip.library.usermanagement.model",  "ip.sms.smsapp.sms.model", "net.intelligentprojects.fcm", "net.intelligentprojects.thawanipayment","net.intelligentproject.daleelideliverylibrary"})
@OpenAPIDefinition(info = @Info(title = "XGarage kernel-service API Docs", version = "1.0", description = "XGarage kernel-service API Docs"))
public class XgarageKernelAppApplication {


	private final static Logger logger = LoggerFactory.getLogger("XGarage Logger");

	public static void main(String[] args) {

		SpringApplication.run(XgarageKernelAppApplication.class, args);
		LocalDateTime timestamp = LocalDateTime.now();
		log.info("Now in The Server: " + timestamp);

	}

	@Bean
	CacheManager cacheManager(){return new ConcurrentMapCacheManager();}


	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(basePackages = {"common.repository", "ip.library.usermanagement.repository", "ip.sms.smsapp.sms.repository", "net.intelligentprojects.thawanipayment","net.intelligentproject.daleelideliverylibrary", "net.intelligentprojects.fcm"})
	@ComponentScan(basePackages = {"ip.library.usermanagement", "ip.sms.smsapp.sms", "net.intelligentprojects.fcm", "net.intelligentproject.daleelideliverylibrary", "net.intelligentprojects.thawanipayment"})
	class GlobalConfig {
	}
}
