package br.com.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import lombok.ToString;

@Configuration
@ToString
@ConfigurationProperties(prefix ="jwt.config")
public class JWTConfiguration {

	private String loginUrl = "/login";
	
	@NestedConfigurationProperty
	private Header header = new Header();
	
	private int expiration = 3600;	
	private String privateKey = "Bx38Wr4yLg0PmidXqx2dHBXjfemIfo4iAUechE3sC3I=";
	private String type = "encrypted";
	
		
	public String getLoginUrl() {
		return loginUrl;
	}


	public Header getHeader() {
		return header;
	}


	public int getExpiration() {
		return expiration;
	}


	public String getPrivateKey() {
		return privateKey;
	}


	public String getType() {
		return type;
	}


	public static class Header{
		private String name = "Authorization";
		private String prefix = "Bearer ";
		
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}
		@SuppressWarnings("unused")
		public String getPrefix() {
			return prefix;
		}
	}
	
	
}
