package com.ai;

import java.awt.image.BufferedImage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AimlserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AimlserviceApplication.class, args);
	}

	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	
	@Bean
	public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
		return new BufferedImageHttpMessageConverter();
	}
	
	
	
}
