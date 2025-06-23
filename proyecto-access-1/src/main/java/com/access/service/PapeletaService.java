package com.access.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.model.Papeleta;
import com.access.repository.PapeletaRepository;

@Service
public class PapeletaService {
	
	  private final PapeletaRepository papeletaRepository;
		private final DetallePapeletaService detallePapeletaService; 

	    public PapeletaService(PapeletaRepository papeletaRepository, DetallePapeletaService detallePapeletaService) {
	        this.papeletaRepository = papeletaRepository;
	        this.detallePapeletaService = detallePapeletaService;
	    }
	    
	    public Papeleta addPapeleta(Papeleta papeleta) {
	    	if(getPapeletasByFolio(papeleta.getFolio()).isEmpty()) {
	    		papeletaRepository.addPapeleta(papeleta);
		        return papeleta;
	    	}
	    	else return new Papeleta();
	    }
	    
	    public List<Papeleta> getAllPapeletas() {
	        List<Papeleta> papeletas = papeletaRepository.getAllPapeletas();
	        return papeletas;
	    }
	    
	    public List<Papeleta> getPapeletasByFolio(Integer folio) {
	    	List<Papeleta> papeletas = papeletaRepository.getPapeletasByFolio(folio);
	    	for (Papeleta papeleta : papeletas) {
	            papeleta.setDetallepapeleta(detallePapeletaService.getDetallePapeleta(papeleta.getFolio()));
	        }
	        return papeletas;
	    }
	    	    
	    public PaginationResult<List<Papeleta>> getPapeletasFiltradas(PapeletaPaginationDTO dto) {
	        int pageValue = dto.getPage();
	        int limitValue = dto.getLimit();
	        int offset = (pageValue - 1) * limitValue;
	    	
	        StringBuilder sql = new StringBuilder();
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
	        
	        int totalItems = papeletaRepository.contarElementosPapeleta(sql.toString(), params);
	        
	        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	        
	        List<Papeleta> data = papeletaRepository.getPapeletasList(sql.toString(), params, limitValue, offset);
	        for (Papeleta papeleta : data) {
	            papeleta.setDetallepapeleta(detallePapeletaService.getDetallePapeleta(papeleta.getFolio()));
	        }
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }
	   
}
