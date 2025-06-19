package com.access.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.bitacoras.BitacoraProdListadoDTO;
import com.access.model.BitacoraTiempos;
import com.access.repository.BitacoraTiempoRepository;

@Service
public class BitacoraTiempoService {
	
	private final BitacoraTiempoRepository bitacoraTiempoRepository;
	
	public BitacoraTiempoService(BitacoraTiempoRepository bitacoraTiempoRepository) {
		this.bitacoraTiempoRepository = bitacoraTiempoRepository;
	}
	
	public void insertarRegistro(Integer folio, String etapa, String movimiento, String usuario) {
		Date fechaActual = Date.valueOf(LocalDate.now());
		bitacoraTiempoRepository.insertarRegistro(folio, fechaActual, etapa, movimiento, usuario);
	}
	
	public PaginationResult<List<BitacoraTiempos>> getListadoBitacoraProd(BitacoraProdListadoDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
	    StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getId() != null) {
            sql.append(" AND Id = ?");
            params.add(dto.getId());
        }
        
        if (dto.getFolio() != null) {
            sql.append(" AND Folio = ?");
	        params.add(dto.getFolio());
        }
        
        if (dto.getEtapa() != null) {
            sql.append(" AND Etapa = ?");
	        params.add(dto.getEtapa());
        }     
        
        if (dto.getMovimiento() != null) {
            sql.append(" AND Movimiento LIKE ?");
            params.add("%" + dto.getMovimiento() + "%");
        }
        
        if (dto.getUsuario() != null) {
            sql.append(" AND Usuario = ?");
	        params.add(dto.getUsuario());
        }
        
        // Conteo total
        int totalItems = bitacoraTiempoRepository.contarElementosBitacoraProd(sql.toString(), params);

        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
        List<BitacoraTiempos> data = bitacoraTiempoRepository.getBitacoraProduccion(sql.toString(), params, limitValue, offset);
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
}
