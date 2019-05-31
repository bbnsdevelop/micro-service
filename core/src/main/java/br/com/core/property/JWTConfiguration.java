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
	private String privateKey = "XJ7jyHG4y39ZGmQVxygPvDtv16AMZROK";
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


	private static class Header{
		private String name = "Authorization";
		private String prefix = "Bearer ";
		
		public String getName() {
			return name;
		}
		public String getPrefix() {
			return prefix;
		}
	}
	
	
}
