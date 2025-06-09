package com.access.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BitacoraTiempoService {
	
	private final JdbcTemplate jdbcTemplate;
	
	public BitacoraTiempoService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void insertarRegistro(Integer folio, String etapa, String movimiento, String usuario) {
			String sql = "INSERT INTO BitacoraTiempos (Fecha, Folio, Etapa, Movimiento, Usuario) "
			+ "VALUES (?, ?, ?, ?, ?)";
			java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
			jdbcTemplate.update(sql, fechaActual, folio, etapa.toUpperCase(), movimiento.toUpperCase(), usuario.toUpperCase());
	}
}
