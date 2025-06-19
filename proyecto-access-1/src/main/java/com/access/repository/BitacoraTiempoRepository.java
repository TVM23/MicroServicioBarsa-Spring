package com.access.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.BitacoraTiempos;

@Repository
public class BitacoraTiempoRepository {

	private final JdbcTemplate jdbcTemplate;
	
	public BitacoraTiempoRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private BitacoraTiempos convertBitProd(ResultSet rs) throws SQLException{
		BitacoraTiempos bitProd = new BitacoraTiempos();
		bitProd.setId(rs.getInt("Id"));
		bitProd.setFecha(rs.getString("Fecha"));
		bitProd.setFolio(rs.getInt("Folio"));
		bitProd.setEtapa(rs.getString("Etapa"));
		bitProd.setMovimiento(rs.getString("Movimiento"));
		bitProd.setUsuario(rs.getString("Usuario"));
		return bitProd;
	}
	
	public void insertarRegistro(Integer folio, Date fechaActual, String etapa, String movimiento, String usuario) {
		String sql = "INSERT INTO BitacoraTiempos (Fecha, Folio, Etapa, Movimiento, Usuario) "
		+ "VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, fechaActual, folio, etapa.toUpperCase(), movimiento.toUpperCase(), usuario.toUpperCase());
	}
	
	public List<BitacoraTiempos> getBitacoraProduccion(String sqlClauses, List<Object> params, int limitValue, int offset){
		String sql = "SELECT * " +
				" FROM BitacoraTiempos WHERE 1=1 " +
                sqlClauses + " ORDER BY Fecha DESC LIMIT ? OFFSET ?";
		params.add(limitValue);
        params.add(offset);
        List<BitacoraTiempos> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convertBitProd(rs); // tu función de conversión
        }, params.toArray());
        
        return data;
	}
	
	public int contarElementosBitacoraProd(String sqlClauses, List<Object> params) {
        String countSql = "SELECT COUNT(*) AS total  " + 
				" FROM BitacoraTiempos WHERE 1=1 " +
                sqlClauses;
        int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        return count;
	}
	
}
