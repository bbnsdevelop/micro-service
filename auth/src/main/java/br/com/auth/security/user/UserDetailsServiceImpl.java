package br.com.auth.security.user;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.core.model.ApplicationUser;
import br.com.core.repository.ApplicationUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	
	@Autowired
	private ApplicationUserRepository applicationUserRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		log.info("Searching in the DB the user by username -> {}", username);
		ApplicationUser applicationUser = this.applicationUserRepository.findByUsername(username);
		log.info("ApplicationUser found-> {}", applicationUser);
		if(applicationUser == null) {
			throw new UsernameNotFoundException(String.format("Application user '%s' not found", username));
		}
		return new CustomUserDetails(applicationUser);
	}
	
	public static final class CustomUserDetails extends ApplicationUser implements  UserDetails {
		private String username;
		private String password;
		
		public CustomUserDetails(ApplicationUser applicationUser) {
			super(applicationUser);
			this.password = applicationUser.getPassword();
			this.username = applicationUser.getUsername();
		}

		private static final long serialVersionUID = 1L;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + this.getRole());
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
		
	}
	

}
