package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.Papeleta;
import com.access.service.DetallePapeletaService;

@Repository
public class PapeletaRepository {
	
	private final JdbcTemplate jdbcTemplate;
	private final DetallePapeletaService detallePapeletaService; 
	
	public PapeletaRepository(JdbcTemplate jdbcTemplate, DetallePapeletaService detallePapeletaService) {
		this.jdbcTemplate = jdbcTemplate;
        this.detallePapeletaService = detallePapeletaService;
	}
	
	private Papeleta convert(ResultSet rs) throws SQLException {
   	 Papeleta papeleta = new Papeleta();
           papeleta.setTipoId(rs.getString("TipoId"));
           papeleta.setFolio(rs.getInt("Folio"));
           papeleta.setFecha(rs.getString("Fecha"));
           papeleta.setStatus(rs.getString("Status"));
           papeleta.setObservacionGeneral(rs.getString("ObservacionGeneral"));
           papeleta.setDetallepapeleta(
           		detallePapeletaService.getDetallePapeleta(papeleta.getFolio())
           		);
           return papeleta;
   }
	
	public List<Papeleta> getAllPapeletas() {
        String sql = "SELECT * FROM Papeleta";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        });
    }
	
	public List<Papeleta> getPapeletasByFolio(Integer folio) {
        String sql = "SELECT * FROM Papeleta where Folio = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, folio);
    }
	
	public List<Papeleta> getPapeletasList(String sqlClauses, List<Object> params, int limitValue, int offset){
		String sql = "SELECT * " +
				"FROM Papeleta WHERE 1=1 "+
                sqlClauses + " LIMIT ? OFFSET ?";
		params.add(limitValue);
        params.add(offset);
        List<Papeleta> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs); // tu función de conversión
        }, params.toArray());
        
        return data;
	}
	
	public int contarElementosPapeleta(String sqlClauses, List<Object> params) {
        String countSql = "SELECT COUNT(*) AS total " + 
				"FROM Papeleta WHERE 1=1 " +
                sqlClauses;
        int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        return count;
	}
	
	public void addPapeleta(Papeleta papeleta) {
    	String sql = "INSERT INTO Papeleta (TipoId,Folio,Fecha,Status,ObservacionGeneral) VALUES (?,?,?,?,?)";
	        jdbcTemplate.update(sql,
	        		papeleta.getTipoId(),
	        		papeleta.getFolio(),
	        		papeleta.getFecha(),
	        		papeleta.getStatus(),
	        		papeleta.getObservacionGeneral()
	        		);
    }
}
