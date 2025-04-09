package com.access.service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.colores.CreateColorDTO;
import com.access.dto.materia.CreateMateriaDTO;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.model.Colores;
import com.access.model.Materia;
import com.access.model.Producto;
import com.access.service.cloudinary.CloudinaryService;

import jakarta.annotation.PostConstruct;

@Service
public class MateriaService {
	
	  @Autowired
	  private CloudinaryService cloudinaryService;
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
	    
	    public List<Materia> getMateriaByDescripcion(String descripcion) {
	        String sql = "SELECT * FROM Materia where Descripcion = ?";	       
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs);
	        }, descripcion);
	    }
	    
	    public List<Materia> getMateriaByCodigo(String codigo) {
	        String sql = "SELECT * FROM Materia where CodigoMat = ?";	       
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs);
	        }, codigo);
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
	    		        
	        StringBuilder sql = new StringBuilder("FROM Materia WHERE 1=1");
		    List<Object> params = new ArrayList<>(); // Lista para almacenar los parámetros
	        
	        if (dto.getCodigoMat() != null) {
	            sql.append(" AND CodigoMat = ?");
		        params.add(dto.getCodigoMat());
	        }
	        if (dto.getDescripcion() != null) {
	            sql.append(" AND Descripcion LIKE ?");
		        params.add("%" + dto.getDescripcion() + "%");
	        }
	        if (dto.getUnidad() != null) {
	            sql.append(" AND Unidad = ?");
		        params.add(dto.getUnidad());
	        }
	        if (dto.getProceso() != null) {
	            sql.append(" AND Proceso = ?");
		        params.add(dto.getProceso());
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
		    List<Materia> data = jdbcTemplate.query(paginatedSql, (rs, rowNum) -> {
		        return convert(rs);
		    }, params.toArray());
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }
	    
	    public List<Materia> getMateriasByCodigoMat(String codigo) {
	    	String sql = "SELECT * FROM Materia WHERE CodigoMat = ?";
	    	return jdbcTemplate.query(sql, (rs, rowNum) -> convert(rs), codigo);
	    }

	    public ResponseEntity<?> createNewMateria(CreateMateriaDTO dto) {
		    if(getMateriaByDescripcion(dto.getDescripcion()).isEmpty()) {
		    	if(getMateriaByCodigo(dto.getCodigoMat()).isEmpty()) {
		    		
		    		// Subir imágenes a Cloudinary
		            List<String> urls = new ArrayList<>();
		            if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
		                urls = cloudinaryService.uploadBase64Images(dto.getImagenes());
		            }
		    		
		    		String sql = "INSERT INTO Materia (CodigoMat, Descripcion, Unidad, PCompra, Existencia, Max, Min, "
			    			+ "InventarioInicial, UnidadEntrada, CantXUnidad, Proceso, Borrado) VALUES "
			    			+ "(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			        jdbcTemplate.update(sql,
			        		dto.getCodigoMat(),
			        		dto.getDescripcion(),
			        		dto.getUnidad(),
			        		dto.getPcompra(),
			        		dto.getExistencia(),
			        		dto.getMax(),
			        		dto.getMin(),
			        		dto.getInventarioInicial(),
			        		dto.getUnidadEntrada(),
			        		dto.getCantxunidad(),
			        		dto.getProceso(),
			        		dto.getBorrado()
			        		);
			        
			        // Guardar las URLs de las imágenes, puedes hacerlo en otra tabla (ImagenMateria)
		            for (String url : urls) {
		                String sqlImg = "INSERT INTO ImagenMateria (CodigoMat, ImagenUrl) VALUES (?, ?)";
		                jdbcTemplate.update(sqlImg, dto.getCodigoMat(), url);
		            }
			        
			        return ResponseEntity.ok(Map.of("message", "Materia creada correctamente"));
		    	}else {
		    		return ResponseEntity
		    	            .status(HttpStatus.BAD_REQUEST)
		    	            .body(Map.of("error", "Ya existe una materia con ese codigo"));
		    	}
	    	}
	    	else {
	    		return ResponseEntity
	    	            .status(HttpStatus.BAD_REQUEST)
	    	            .body(Map.of("error", "Ya existe una materia con esa descripcion"));
	    	}
	    }

	    
	    public List<Materia> deleteMateria(String codigo) {
	    	deleteIndexMateria();

	        String sql = "UPDATE Materia set Borrado=true where CodigoMat='"+codigo+"'";
	        jdbcTemplate.update(sql);
	        createIndexMateria();

	        return getMateriasByCodigoMat(codigo);
	    }
	    
	    private void deleteIndexMateria() {
	    	jdbcTemplate.execute("ALTER TABLE Materia DROP PRIMARY KEY");
	    	jdbcTemplate.execute("DROP INDEX PrimaryKey on Materia");

	    }
	    
	    private void createIndexMateria() {
	    	
	    	jdbcTemplate.execute("ALTER TABLE Materia ADD CONSTRAINT PrimaryKey PRIMARY KEY (CodigoMat);");
	    	jdbcTemplate.execute("CREATE UNIQUE INDEX PrimaryKey on Materia (CodigoMat ASC)");
	    }

}
