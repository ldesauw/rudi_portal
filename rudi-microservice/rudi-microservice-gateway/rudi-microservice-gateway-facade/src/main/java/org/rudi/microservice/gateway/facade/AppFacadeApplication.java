package org.rudi.microservice.gateway.facade;

import org.rudi.common.core.yml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.microservice.gateway.facade" })
@EnableEurekaClient
@EnableScheduling
@EnableCircuitBreaker
@PropertySource(value = { "classpath:gateway-common.properties" }, ignoreResourceNotFound = false)
@PropertySource(value = { "file:${rudi.config}/gateway/gateway.properties" }, ignoreResourceNotFound = true)
@PropertySource(value = {
		"file:${rudi.config}/gateway/gateway.yml" }, ignoreResourceNotFound = true, factory = YamlPropertySourceFactory.class)
public class AppFacadeApplication {

	public static void main(final String[] args) {
		System.setProperty("spring.config.name", "gateway");
		SpringApplication.run(AppFacadeApplication.class, args);
	}

}