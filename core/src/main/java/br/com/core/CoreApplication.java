package br.com.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.com.core.model.ApplicationUser;
import br.com.core.repository.ApplicationUserRepository;

@SpringBootApplication
public class CoreApplication {
	
	@Autowired
	private ApplicationUserRepository applicationUserRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}
	
	/*@Bean
	public void saveUser() {
		ApplicationUser user = new ApplicationUser();
		user.setId(1000L);
		user.setPassword("$2a$10$6tz8VJ8ScB4IZN6u2qLf6uBRt1qTKFTEkPvcDZnsqwKglqauEW.yK");
		user.setUsername("snow");
		user.setRole("ADMIN");
		this.applicationUserRepository.save(user);
	}*/
	/*@Bean
	public void delete() {
		this.applicationUserRepository.deleteAll();
		this.applicationUserRepository.findAll();
	}*/

}
