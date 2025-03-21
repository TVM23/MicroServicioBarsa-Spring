package com.access.service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	    	
	        StringBuilder sql = new StringBuilder("FROM Papeleta WHERE 1=1");
	        List<Object> params = new ArrayList<>();
	        
	        if (dto.getFolio() != null) {
	        	sql.append(" AND Folio = ?");
	        	params.add(dto.getFolio());
	        }
	        if (dto.getTipoId() != null) {
	        	sql.append(" AND TipoId = ?");
	        	params.add(dto.getTipoId());
	        }
	        if (dto.getFecha() != null) {
	            sql.append(" AND Fecha = ?");
		        params.add(dto.getFecha());
	        }
	        if (dto.getStatus() != null) {
	        	sql.append(" AND Status = ?");
	        	params.add(dto.getStatus());
	        }
	        if (dto.getObservacionGeneral() != null) {
	            sql.append(" AND ObservacionGeneral LIKE ?");
		        params.add("%" + dto.getObservacionGeneral() + "%");
	        }
	        
	        String countSql = "SELECT COUNT(*) AS total " + sql.toString();
	        int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
	        
	        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	        
	        String paginataSql = "Select * " + sql.toString() + " LIMIT ? OFFSET ?";
	        params.add(limitValue);
	        params.add(offset);
	  
	        List<Papeleta> data = jdbcTemplate.query(paginataSql,  (rs, rowNum) -> {
	        	return convert(rs);
	        }, params.toArray());
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }
	    

}
