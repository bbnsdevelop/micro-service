package br.com.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.core.property.JWTConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(JWTConfiguration.class)
@EntityScan({"br.com.core.model"})
@EnableJpaRepositories({"br.com.core.repository"})
public class CourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}

}
