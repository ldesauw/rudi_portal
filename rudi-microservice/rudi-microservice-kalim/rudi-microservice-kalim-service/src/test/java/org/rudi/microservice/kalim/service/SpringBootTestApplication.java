package org.rudi.microservice.kalim.service;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.facet.apimaccess",
        "org.rudi.facet.acl",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.kos",
		"org.rudi.facet.providers",
		"org.rudi.microservice.kalim.service",
		"org.rudi.microservice.kalim.storage" })
@PropertySource(value = { "classpath:kalim_test.properties" }, ignoreResourceNotFound = false)
public class SpringBootTestApplication {

	public static void main(final String[] args) {

		SpringApplication.run(SpringBootTestApplication.class, args);

	}

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}

}