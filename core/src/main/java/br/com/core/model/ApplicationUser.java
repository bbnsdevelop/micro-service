package br.com.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.core.model.abstractentity.AbstractEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name ="user")
public class ApplicationUser implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	protected Long id;
	
	@NotNull(message ="the filed title is mandatory")
	@Column(nullable = false)
	private String username;
	
	@NotNull(message ="the filed password is mandatory")
	@Column(nullable = false)
	@ToString.Exclude
	private String password;
	
	@NotNull(message ="the filed role is mandatory")
	@Column(nullable = false)
	private String role ;
	
	
	
	public ApplicationUser() {

	}

	public ApplicationUser (ApplicationUser applicationUser) {
		this.id = applicationUser.getId();
		this.username = applicationUser.getUsername();
		this.password = applicationUser.getPassword();
		this.role = applicationUser.getRole();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		role = "USER";
		this.role = role;
	}
	
	

}
