package com.access.service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.model.Materia;

@Service
public class MateriaService {
	
	  private final JdbcTemplate jdbcTemplate;

	    public MateriaService(JdbcTemplate jdbcTemplate) {
	        this.jdbcTemplate = jdbcTemplate;
	    }
	    
	    private Materia convert(ResultSet rs) throws SQLException {
	    	 Materia materia = new Materia();
	            materia.setCodigoMat(rs.getString("codigoMat"));
	            materia.setUnidad(rs.getString("Unidad"));
	            materia.setDescripcion(rs.getString("Descripcion"));
	            materia.setPCompra(rs.getDouble("PCompra"));
	            materia.setExistencia(rs.getInt("Existencia"));
	            materia.setMax(rs.getInt("Max"));
	            materia.setMin(rs.getInt("Min"));
	            materia.setInventarioInicial(rs.getInt("InventarioInicial"));
	            materia.setUnidadEntrada(rs.getString("UnidadEntrada"));
	            materia.setCantXUnidad(rs.getInt("CantXUnidad"));
	            materia.setProceso(rs.getString("Proceso"));
	            materia.setBorrado(rs.getBoolean("Borrado"));
	            return materia;
	    }

	    public List<Materia> getAllMaterias() {
	        String sql = "SELECT * FROM Materia where Unidad='PZAS'";
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs);
	        });
	    }
	    
	    public PaginationResult<List<Materia>> getMateriasFiltradas(MateriaPaginationDTO dto) {
	        int pageValue = dto.getPage();
	        int limitValue = dto.getLimit();
	        int offset = (pageValue - 1) * limitValue;
	    	
	        String sql = "FROM Materia WHERE 1=1";
	        
	        if (dto.getCodigoMat() != null) {
	            sql += " AND CodigoMat = '" + dto.getCodigoMat() + "'";
	        }
	        if (dto.getDescripcion() != null) {
	            sql += " AND Descripcion LIKE '%" + dto.getDescripcion() + "%'";
	        }
	        if (dto.getUnidad() != null) {
	            sql += " AND Unidad = '" + dto.getUnidad() + "'";
	        }
	        if (dto.getProceso() != null) {
	            sql += " AND Proceso = '" + dto.getProceso() + "'";
	        }
	        if (dto.getBorrado() != null) {
	            sql += " AND Borrado = " + (dto.getBorrado() ? "true" : "false");
	        }
	        
	        SqlRowSet  totalResult = jdbcTemplate.queryForRowSet("SELECT COUNT(*) AS total " + sql);
	        int totalItems = 0;
	        if (totalResult.next()) {
	            totalItems = totalResult.getInt("total");
	        }
	        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	  
	        sql = "Select * " + sql + " LIMIT " + limitValue + " OFFSET "+offset;
	        List<Materia> data = jdbcTemplate.query(sql,  (rs, rowNum) -> {
	        	return convert(rs);
	        });
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }
	    
	    public List<Materia> getMateriasByCodigoMat(String codigo) {
	    	String sql = "SELECT * FROM Materia WHERE CodigoMat = ?";
	    	return jdbcTemplate.query(sql, (rs, rowNum) -> convert(rs), codigo);
	    }
	    
	    private void deleteIndexMateria() {
	    	jdbcTemplate.execute("ALTER TABLE Materia DROP PRIMARY KEY");
	    	jdbcTemplate.execute("DROP INDEX PrimaryKey on Materia");

	    }
	    
	    private void createIndexMateria() {
	    	
	    	jdbcTemplate.execute("ALTER TABLE Materia ADD CONSTRAINT PrimaryKey PRIMARY KEY (CodigoMat);");
	    	jdbcTemplate.execute("CREATE UNIQUE INDEX PrimaryKey on Materia (CodigoMat ASC)");
	    }
	    
	    public Materia addMateria(Materia materia) {
	    	
	    	if(getMateriasByCodigoMat(materia.getCodigoMat()).isEmpty()) {
		        String sql = "INSERT INTO Materia (CodigoMat,Descripcion,Unidad,PCompra,Existencia,Max,Min,InventarioInicial,UnidadEntrada,CantXUnidad,Proceso,Borrado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		        jdbcTemplate.update(sql,
		        		materia.getCodigoMat(),
		        		materia.getDescripcion(),
		        		materia.getUnidad(),
		        		materia.getPCompra(),
		        		materia.getExistencia(),
		        		materia.getMax(),
		        		materia.getMin(),
		        		materia.getInventarioInicial(),
		        		materia.getUnidadEntrada(),
		        		materia.getCantXUnidad(),
		        		materia.getProceso(),
		        		materia.getBorrado()
		        		);		        
	        return materia;
	    	}
	    	else return new Materia();
	    }

	    
	    public List<Materia> deleteMateria(String codigo) {
	    	deleteIndexMateria();

	        String sql = "UPDATE  Materia set Borrado=true where CodigoMat='"+codigo+"'";
	        jdbcTemplate.update(sql);
	        createIndexMateria();

	        return getMateriasByCodigoMat(codigo);
	    }

}
