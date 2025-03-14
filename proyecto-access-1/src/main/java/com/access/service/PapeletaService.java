package com.access.service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.model.Materia;
import com.access.model.Papeleta;

@Service
public class PapeletaService {
	
	  private final JdbcTemplate jdbcTemplate;
	  private final DetallePapeletaService detallePapeletaService; 

	    public PapeletaService(JdbcTemplate jdbcTemplate, DetallePapeletaService detallePapeletaService) {
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
	        String sql = "SELECT * FROM Papeleta where Folio=" + folio + "";	       
	        
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs);
	        });
	    }
	    
	    public Papeleta addPapeleta(Papeleta papeleta) {
	    	if(getPapeletasByFolio(papeleta.getFolio()).isEmpty()) {
	    		String sql = "INSERT INTO Papeleta (TipoId,Folio,Fecha,Status,ObservacionGeneral) VALUES (?,?,?,?,?)";
		        jdbcTemplate.update(sql,
		        		papeleta.getTipoId(),
		        		papeleta.getFolio(),
		        		papeleta.getFecha(),
		        		papeleta.getStatus(),
		        		papeleta.getObservacionGeneral()
		        		);
		        return papeleta;
	    	}
	    	else return new Papeleta();
	    }
	    
	    public PaginationResult<List<Papeleta>> getPapeletasFiltradas(PapeletaPaginationDTO dto) {
	        int pageValue = dto.getPage();
	        int limitValue = dto.getLimit();
	        int offset = (pageValue - 1) * limitValue;
	    	
	        String sql = "FROM Papeleta WHERE 1=1";
	        
	        if (dto.getFolio() != null) {
	            sql += " AND Folio = " + dto.getFolio() + "";
	        }
	        if (dto.getTipoId() != null) {
	            sql += " AND TipoId = '" + dto.getTipoId() + "'";
	        }
	        if (dto.getFecha() != null) {
	            sql += " AND Fecha = #" + dto.getFecha() + "#";
	        }
	        if (dto.getStatus() != null) {
	            sql += " AND Status = '" + dto.getStatus() + "'";
	        }
	        if (dto.getObservacionGeneral() != null) {
	            sql += " AND ObservacionGeneral LIKE '%" + dto.getObservacionGeneral() + "%'";
	        }
	        
	        SqlRowSet  totalResult = jdbcTemplate.queryForRowSet("SELECT COUNT(*) AS total " + sql);
	        int totalItems = 0;
	        if (totalResult.next()) {
	            totalItems = totalResult.getInt("total");
	        }
	        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	  
	        sql = "Select * " + sql + " LIMIT " + limitValue + " OFFSET "+offset;
	        List<Papeleta> data = jdbcTemplate.query(sql,  (rs, rowNum) -> {
	        	return convert(rs);
	        });
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }
	    

}
