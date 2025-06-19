package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.dto.colores.CreateColorDTO;
import com.access.model.Colores;

@Repository
public class ColoresRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public ColoresRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Colores convert(ResultSet rs) throws SQLException {
		Colores color = new Colores();
		color.setColorId(rs.getInt("ColorId"));
		color.setDescripcion(rs.getString("Descripcion"));
		color.setBorrado(rs.getBoolean("Borrado"));
		return color;
	}
	
	public void createNewColor(CreateColorDTO dto) {
	    String sql = "INSERT INTO Colores (Descripcion, Borrado) VALUES (?,?)";
	    jdbcTemplate.update(sql,
	        dto.getDescripcion(),
	        dto.getBorrado()
	        );
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
	
	public List<Colores> getColorDescripcionByCodigo(Integer colorId) {
        String sql = "SELECT Descripcion FROM Colores where ColorId = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, colorId);
    }
	
	public List<Colores> getColoresList(String sqlClauses, List<Object> params, int limitValue, int offset){
		String sql = "SELECT * " +
				"FROM Colores WHERE 1=1 "+
                sqlClauses + " LIMIT ? OFFSET ?";
		params.add(limitValue);
        params.add(offset);
        List<Colores> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs); // tu función de conversión
        }, params.toArray());
        
        return data;
	}
	
	public int contarElementosColores(String sqlClauses, List<Object> params) {
        String countSql = "SELECT COUNT(*) AS total " + 
				"FROM Colores WHERE 1=1 " +
                sqlClauses;
        int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        return count;
	}
}
