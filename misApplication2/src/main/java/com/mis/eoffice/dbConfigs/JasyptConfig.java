package com.mis.eoffice.dbConfigs;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JasyptConfig {
	
	    @Value("${jasypt.encryptor.password}") // This value should be set in your application.properties
	    private String jasyptEncryptorPassword;

	    @Bean(name = "jasyptStringEncryptor")
	    @Primary
	    public StringEncryptor stringEncryptor() {
	        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	        encryptor.setPassword(jasyptEncryptorPassword);
	        return encryptor;
	    }
	

}
