package com.access.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;

@Component
public class DataBaseWarmUp {

    @Autowired
    private HikariDataSource dataSource;
    
    @PostConstruct
    public void warmUp() {
    	try {
			dataSource.getConnection().close();
			System.out.println("Inicio Calentamiento");
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}