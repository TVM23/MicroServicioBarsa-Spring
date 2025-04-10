package com.access.service;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.access.dto.PaginationResult;
import com.access.dto.colores.ColoresPaginationDTO;
import com.access.dto.colores.CreateColorDTO;
import com.access.model.Colores;
import com.access.model.Papeleta;

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
	
	public List<Colores> getColorByCodigo(Integer colorId) {
        String sql = "SELECT * FROM Colores where ColorId = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, colorId);
    }
	
	public List<Colores> getColorByDescripcion(String descripcion) {
        String sql = "SELECT * FROM Colores where Descripcion = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, descripcion);
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
	
	public List<Colores> getColorDescripcionByCodigo(Integer colorId) {
        String sql = "SELECT Descripcion FROM Colores where ColorId = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, colorId);
    }
	
	public ResponseEntity<?> createNewColor(CreateColorDTO dto) {
	    if(getColorByDescripcion(dto.getDescripcion()).isEmpty()) {
	    	String sql = "INSERT INTO Colores (Descripcion, Borrado) VALUES (?,?)";
	        jdbcTemplate.update(sql,
	        		dto.getDescripcion(),
	        		dto.getBorrado()
	        		);
	        return ResponseEntity.ok(Map.of("message", "Color creado correctamente"));
    	}
    	else {
    		return ResponseEntity
    	            .status(HttpStatus.BAD_REQUEST)
    	            .body(Map.of("error", "Ya existe un color con esa descripcion"));
    	}
    }
}
