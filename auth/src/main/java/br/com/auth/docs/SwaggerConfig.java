package br.com.auth.docs;

import org.springframework.context.annotation.Configuration;

import br.com.core.docs.BasicSwaggerConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends BasicSwaggerConfig{

	public SwaggerConfig() {
		super("br.com.auth.controllers");
	}
	
	

}
