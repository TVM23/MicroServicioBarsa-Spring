package com.access.service;


import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.model.Papeleta;

@Service
public class PapeletaService {
	
	  private final JdbcTemplate jdbcTemplate;

	    public PapeletaService(JdbcTemplate jdbcTemplate) {
	        this.jdbcTemplate = jdbcTemplate;
	    }

	    public List<Papeleta> getAllMaterias() {
	        String sql = "SELECT * FROM Papeleta where Unidad='PZAS'";
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	        	Papeleta objeto = new Papeleta();
	        	objeto.setCodigoMat(rs.getString("codigoMat"));
	        	objeto.setUnidad(rs.getString("Unidad"));
	        	objeto.setDescripcion(rs.getString("Descripcion"));
	        	objeto.setPCompra(rs.getDouble("PCompra"));
	        	objeto.setExistencia(rs.getInt("Existencia"));
	        	objeto.setMax(rs.getInt("Max"));
	            objeto.setMin(rs.getInt("Min"));
	            objeto.setInventarioInicial(rs.getInt("InventarioInicial"));
	            objeto.setUnidadEntrada(rs.getString("UnidadEntrada"));
	            objeto.setCantXUnidad(rs.getInt("CantXUnidad"));
	            objeto.setProceso(rs.getString("Proceso"));
	            objeto.setBorrado(rs.getBoolean("Borrado"));

	            return objeto;
	        });
	    }
	    
	    public List<Papeleta> getMateriasByDescripcion(String codigo) {
	        String sql = "SELECT * FROM Papeleta where CodigoMat='"+codigo+"'";	       
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	        	Papeleta objeto = new Papeleta();
	        	objeto.setCodigoMat(rs.getString("codigoMat"));
	        	objeto.setUnidad(rs.getString("Unidad"));
	        	objeto.setDescripcion(rs.getString("Descripcion"));
	        	objeto.setPCompra(rs.getDouble("PCompra"));
	        	objeto.setExistencia(rs.getInt("Existencia"));
	        	objeto.setMax(rs.getInt("Max"));
	            objeto.setMin(rs.getInt("Min"));
	            objeto.setInventarioInicial(rs.getInt("InventarioInicial"));
	            objeto.setUnidadEntrada(rs.getString("UnidadEntrada"));
	            objeto.setCantXUnidad(rs.getInt("CantXUnidad"));
	            objeto.setProceso(rs.getString("Proceso"));
	            objeto.setBorrado(rs.getBoolean("Borrado"));


	            return objeto;
	        });
	        
	        
	    }
	    
	    public Papeleta addPapeleta(Papeleta materia) {
	        String sql = "INSERT INTO Papeleta (CodigoMat,Descripcion,Unidad,PCompra,Existencia,Max,Min,InventarioInicial,UnidadEntrada,CantXUnidad,Proceso,Borrado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
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
	    
	    public List<Papeleta> deletePapeleta(String codigo) {
	        String sql = "UPDATE  Papeleta set Borrado=true where CodigoMat='"+codigo+"'";
	        jdbcTemplate.update(sql);
	        return getMateriasByDescripcion(codigo);
	    }

}
