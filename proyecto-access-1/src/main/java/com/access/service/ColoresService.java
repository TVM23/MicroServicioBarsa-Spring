package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.colores.ColoresPaginationDTO;
import com.access.model.Colores;

@Service
public class ColoresService {
	
	private final JdbcTemplate jdbcTemplate;
	
	public ColoresService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Colores convert(ResultSet rs) throws SQLException {
		Colores color = new Colores();
		color.setColorId(rs.getInt("ColorId"));
		color.setDescripcion(rs.getString("Descripcion"));
		color.setBorrado(rs.getBoolean("Borrado"));
		return color;
	}
	
	public PaginationResult<List<Colores>> getColoresFiltrados(ColoresPaginationDTO dto){
		int pageValue = dto.getPage();
	    int limitValue = dto.getLimit();
	    int offset = (pageValue - 1) * limitValue;

	    // Consulta base
	    StringBuilder sql = new StringBuilder("FROM Colores WHERE 1=1");
	    List<Object> params = new ArrayList<>(); // Lista para almacenar los parámetros

	    // Filtros dinámicos
	    if (dto.getColorId() != null) {
	        sql.append(" AND ColorId = ?");
	        params.add(dto.getColorId());
	    }
	    if (dto.getDescripcion() != null) {
	        sql.append(" AND Descripcion LIKE ?");
	        params.add("%" + dto.getDescripcion() + "%");
	    }
	    if (dto.getBorrado() != null) {
	        sql.append(" AND Borrado = ?");
	        params.add(dto.getBorrado());
	    }

	    // Contar el total de registros
	    String countSql = "SELECT COUNT(*) AS total " + sql.toString();
	    int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());

	    // Calcular el número total de páginas
	    int totalPages = (int) Math.ceil((double) totalItems / limitValue);

	    // Consulta paginada
	    String paginatedSql = "SELECT * " + sql.toString() + " LIMIT ? OFFSET ?";
	    params.add(limitValue); // Agregar LIMIT como parámetro
	    params.add(offset);     // Agregar OFFSET como parámetro

	    // Ejecutar la consulta paginada
	    List<Colores> data = jdbcTemplate.query(paginatedSql, (rs, rowNum) -> {
	        return convert(rs);
	    }, params.toArray());

	    // Retornar el resultado paginado
	    return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
}
