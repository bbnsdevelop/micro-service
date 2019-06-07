package br.com.auth.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.core.model.ApplicationUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/v1/user")
@Api(value = "Endpoints to manage user's information")
public class UserInfoController {
	
	@GetMapping
	@ApiOperation(value = "will retrieve the information from the user available in the token", response = ApplicationUser.class)
	public ResponseEntity<ApplicationUser> getUserInfo(Principal principal){
		ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		
		return ResponseEntity.status(HttpStatus.OK).body(applicationUser);
	}

}
