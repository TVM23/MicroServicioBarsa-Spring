package com.access.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.colores.ColoresPaginationDTO;
import com.access.dto.colores.CreateColorDTO;
import com.access.model.Colores;
import com.access.repository.ColoresRepository;

@Service
public class ColoresService {
	
	private final ColoresRepository coloresRepository;
	
	public ColoresService(ColoresRepository coloresRepository) {
		this.coloresRepository = coloresRepository;
	}
	
	public ResponseEntity<?> createNewColor(CreateColorDTO dto) {
	    if(getColorByDescripcion(dto.getDescripcion()).isEmpty()) {
	    	coloresRepository.createNewColor(dto);
	        return ResponseEntity.ok(Map.of("message", "Color creado correctamente"));
    	}
    	else {
    		return ResponseEntity
    	            .status(HttpStatus.BAD_REQUEST)
    	            .body(Map.of("error", "Ya existe un color con esa descripcion"));
    	}
    }
	
	public List<Colores> getColorByCodigo(Integer colorId) {
        return coloresRepository.getColorByCodigo(colorId);
    }
	
	public List<Colores> getColorByDescripcion(String descripcion) {
        return coloresRepository.getColorByDescripcion(descripcion);
    }
	
	public List<Colores> getColorDescripcionByCodigo(Integer colorId) {
        return coloresRepository.getColorDescripcionByCodigo(colorId);
    }
	
	public PaginationResult<List<Colores>> getColoresFiltrados(ColoresPaginationDTO dto){
		int pageValue = dto.getPage();
	    int limitValue = dto.getLimit();
	    int offset = (pageValue - 1) * limitValue;

	    // Consulta base
	    StringBuilder sql = new StringBuilder();
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
	    int totalItems = coloresRepository.contarElementosColores(sql.toString(), params);

	    // Calcular el número total de páginas
	    int totalPages = (int) Math.ceil((double) totalItems / limitValue);

	    // Consulta paginada
        List<Colores> data = coloresRepository.getColoresList(sql.toString(), params, limitValue, offset);

	    // Retornar el resultado paginado
	    return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
}
