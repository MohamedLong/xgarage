package com.xgarage;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.time.LocalDateTime;


@SpringBootApplication
@Slf4j
@EnableTransactionManagement
@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "XGarage core-service API Docs", version = "1.0", description = "XGarage core-service API Docs"))
public class XgarageAppApplication {


	private final static Logger logger = LoggerFactory.getLogger("XGarage Logger");

	public static void main(String[] args) {

		SpringApplication.run(XgarageAppApplication.class, args);
		LocalDateTime timestamp = LocalDateTime.now();
		log.info("Now in The Server: " + timestamp);
	}

}