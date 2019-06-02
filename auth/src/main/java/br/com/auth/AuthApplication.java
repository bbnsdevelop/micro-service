package br.com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.core.property.JWTConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(JWTConfiguration.class)
@EntityScan({"br.com.core.model"})
@EnableJpaRepositories({"br.com.core.repository"})
@EnableEurekaClient
@ComponentScan({"br.com.token.security.token"})
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
