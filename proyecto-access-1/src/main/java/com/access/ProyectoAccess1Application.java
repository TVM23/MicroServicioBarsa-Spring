package com.access;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
public class ProyectoAccess1Application {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoAccess1Application.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
