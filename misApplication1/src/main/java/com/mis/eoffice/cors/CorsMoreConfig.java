package com.mis.eoffice.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Profile("development")
public class CorsMoreConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:8080", "http://localhost:3000",
				"http://11.0.0.119:8080","http://localhost/").allowedMethods("GET").allowedMethods("POST").exposedHeaders("Authorization");;
	}
}
