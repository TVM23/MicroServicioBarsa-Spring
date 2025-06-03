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
import org.springframework.stereotype.Service;

import com.access.dto.ImagenDTO;
import com.access.dto.PaginationResult;
import com.access.dto.materia.CreateMateriaDTO;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.model.Imagen;
import com.access.model.Materia;
import com.access.service.cloudinary.CloudinaryService;


@Service
public class MateriaService {
	
	  @Autowired
	  private CloudinaryService cloudinaryService;
	  private final JdbcTemplate jdbcTemplate;
	  private final PapeletaService papeletaService;
	  private final BitacoraService bitacoraservice;
	  private final NotificacionService notificacionService;
	  	  
	    public MateriaService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService, BitacoraService bitacoraService, 
	    		NotificacionService notificacionService) {
	        this.jdbcTemplate = jdbcTemplate;
	        this.papeletaService = papeletaService;
	        this.bitacoraservice = bitacoraService;
	        this.notificacionService = notificacionService;
	    }
	    
	    private Materia convert(ResultSet rs) throws SQLException {
	    	 Materia materia = new Materia();
	            materia.setCodigoMat(rs.getString("CodigoMat"));
	            materia.setUnidad(rs.getString("Unidad"));
	            materia.setDescripcion(rs.getString("Descripcion"));
	            materia.setPCompra(rs.getDouble("PCompra"));
	            materia.setExistencia(rs.getDouble("Existencia"));
	            materia.setMax(rs.getDouble("Max"));
	            materia.setMin(rs.getDouble("Min"));
	            materia.setInventarioInicial(rs.getDouble("InventarioInicial"));
	            materia.setUnidadEntrada(rs.getString("UnidadEntrada"));
	            materia.setCantXUnidad(rs.getDouble("CantXUnidad"));
	            materia.setProceso(rs.getString("Proceso"));
	            materia.setBorrado(rs.getBoolean("Borrado"));
	            materia.setImagenes(this.getImagenesMateria(materia.getCodigoMat()));
	            return materia;
	    }
	    
	    private Imagen convertImg(ResultSet rs) throws SQLException {
	    	 Imagen img = new Imagen();
	    	 img.setId(rs.getInt("Id"));
	    	 img.setCodigoMat(rs.getString("CodigoMat"));
	    	 img.setUrl(rs.getString("ImagenUrl"));
	    	 img.setPublic_id(rs.getString("Public_Id"));
	    	 return img;
	    }
	    
	    public List<Imagen> getImagenesMateria(String codigo){
	    	String sql = "SELECT * FROM ImagenMateria WHERE CodigoMat = ?";
	    	return jdbcTemplate.query(sql, (rs, rowNum) -> {
	    		return convertImg(rs);
	    	}, codigo);
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
	    
	    public PaginationResult<List<Materia>> getMateriasFiltradas(MateriaPaginationDTO dto) {
	        int pageValue = dto.getPage();
	        int limitValue = dto.getLimit();
	        int offset = (pageValue - 1) * limitValue;
	    		        
	        StringBuilder sql = new StringBuilder("FROM Materia WHERE 1=1");
		    List<Object> params = new ArrayList<>(); // Lista para almacenar los parámetros
	        
	        if (dto.getCodigoMat() != null) {
	            sql.append(" AND CodigoMat LIKE ?");
		        params.add("%" + dto.getCodigoMat() + "%");
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
	    

	    public ResponseEntity<?> createNewMateria(CreateMateriaDTO dto, String usuario) {
		    if(getMateriaByDescripcion(dto.getDescripcion()).isEmpty()) {
		    	if(getMateriaByCodigo(dto.getCodigoMat()).isEmpty()) {
		    		
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
			        
			        // Guardar las URLs de las imágenes
			        if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
			        	for (ImagenDTO img : dto.getImagenes()) {
			                String sqlImg = "INSERT INTO ImagenMateria (CodigoMat, ImagenUrl, Public_Id) VALUES (?, ?, ?)";
			                jdbcTemplate.update(sqlImg, dto.getCodigoMat(), img.getUrl(), img.getPublic_id());
			            }
		            }
			        bitacoraservice.registroInventario(true, dto.getCodigoMat(), usuario, dto.getExistencia(), 0.0, null);
			        //notificacionService.evaluarNotificacion(dto.getCodigoMat());
			        return ResponseEntity.ok(Map.of("message", "Materia creada correctamente"));
		    	}else {
		    		this.deleteImagesDueError(dto);
		    		return ResponseEntity
		    	            .status(HttpStatus.BAD_REQUEST)
		    	            .body(Map.of("error", "Ya existe una materia con ese codigo"));
		    	}
	    	}
	    	else {
	    		this.deleteImagesDueError(dto);
	    		return ResponseEntity
	    	            .status(HttpStatus.BAD_REQUEST)
	    	            .body(Map.of("error", "Ya existe una materia con esa descripcion"));
	    	}
	    }
	    
	    public ResponseEntity<?> updateMateria(CreateMateriaDTO dto, String usuario) {
	    	List<Materia> materiaConsulta = getMateriaByCodigo(dto.getCodigoMat());
	    	if(!materiaConsulta.isEmpty()) {
	    		List<Materia> mat = getMateriaByDescripcion(dto.getDescripcion());
		    	if(mat.isEmpty() || ( mat.size() == 1 && mat.get(0).getCodigoMat().equals(dto.getCodigoMat()) ) ) {
		    		
		    		Double existAnt = materiaConsulta.get(0).getExistencia();
		    		
		    		String codigo = dto.getCodigoMat();

			    	String sql = "UPDATE Materia SET Descripcion = ?, Unidad = ?, PCompra = ?, Existencia = ?, Max = ?, Min = ?, "
				    		+ "InventarioInicial = ?, UnidadEntrada = ?, CantXUnidad = ?, Proceso = ?, Borrado = ? WHERE CodigoMat = ? ";
				    jdbcTemplate.update(sql,
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
				       		dto.getBorrado(),
				        	codigo
				        	);
				        
				    // 🔸 Borrar imágenes anteriores asociadas
				    List<Imagen> imagenesAntiguas = getImagenesMateria(codigo);
				    for (Imagen img : imagenesAntiguas) {
			            String public_id = img.getPublic_id();
			            cloudinaryService.deleteImageCloudinary(public_id);
			        }
	;			    String sqlDelete = "DELETE FROM ImagenMateria WHERE CodigoMat = ?";
				    jdbcTemplate.update(sqlDelete, codigo);
				        
				    // Guardar las URLs de las imágenes	    
				    if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
			        	for (ImagenDTO img : dto.getImagenes()) {
			                String sqlImg = "INSERT INTO ImagenMateria (CodigoMat, ImagenUrl, Public_Id) VALUES (?, ?, ?)";
			                jdbcTemplate.update(sqlImg, dto.getCodigoMat(), img.getUrl(), img.getPublic_id());
			            }
		            }
				    
			        bitacoraservice.registroInventario(false, dto.getCodigoMat(), usuario, dto.getExistencia(), existAnt, null);
			        //notificacionService.evaluarNotificacion(dto.getCodigoMat());
				    return ResponseEntity.ok(Map.of("message", "Materia actualizada correctamente"));
				    
		    	} else {
		    		this.deleteImagesDueError(dto);
		    		return ResponseEntity
		    	            .status(HttpStatus.BAD_REQUEST)
		    	            .body(Map.of("error", "Ya existe una materia con esa descripcion"));
		    	}
	    	}else {
	    		this.deleteImagesDueError(dto);
	    		return ResponseEntity
	    	            .status(HttpStatus.BAD_REQUEST)
	    	            .body(Map.of("error", "No existe ninguna materia con ese codigo"));
	    	}
	    }
	    
	    public ResponseEntity<?> deleteMateria(String codigo, String usuario) {
	    	List<Materia> materia = getMateriaByCodigo(codigo);
	    	if(!materia.isEmpty()) {
		    	Materia mat = materia.get(0);
	    		String sql = "UPDATE Materia set Borrado = true where CodigoMat = ?";
		        jdbcTemplate.update(sql, codigo);
		        
		        // 🔸 Borrar imágenes anteriores asociadas
			    List<Imagen> imagenesAntiguas = getImagenesMateria(codigo);
			    for (Imagen img : imagenesAntiguas) {
		            String public_id = img.getPublic_id();
		            cloudinaryService.deleteImageCloudinary(public_id);
		        }
;			    String sqlDelete = "DELETE FROM ImagenMateria WHERE CodigoMat = ?";
			    jdbcTemplate.update(sqlDelete, codigo);
			    
		        bitacoraservice.registroInventario(false, mat.getCodigoMat(), usuario, mat.getExistencia(), mat.getExistencia(), null);
		        //notificacionService.deleteNotificacionCodigo(codigo);
			    return ResponseEntity.ok(Map.of("message", "Materia borrada correctamente"));
	    	}else {
	    		return ResponseEntity
	    	            .status(HttpStatus.BAD_REQUEST)
	    	            .body(Map.of("error", "No existe ninguna materia con este código"));
	    	}
	    }
	    
	    public void deleteImagesDueError(CreateMateriaDTO dto) {
	    	List<ImagenDTO> imagenesEliminar = dto.getImagenes();
		    for (ImagenDTO img : imagenesEliminar) {
	            String public_id = img.getPublic_id();
	            cloudinaryService.deleteImageCloudinary(public_id);
	        }
	    }
	    
	    public List<Materia> getMateriasNoBorradas() {
	        String sql = "SELECT * FROM Materia where Borrado = false";	       
	        return jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs);
	        });
	    }

}
