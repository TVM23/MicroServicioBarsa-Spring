package com.access.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.access.model.DetallePapeleta;
import com.access.repository.DetallePapeletaRepository;

@Service
public class DetallePapeletaService {
	
	private final DetallePapeletaRepository detallePapeletaRepository;
	
	public DetallePapeletaService(DetallePapeletaRepository detallePapeletaRepository) {
		this.detallePapeletaRepository = detallePapeletaRepository;
	}
		
	public List<DetallePapeleta> getDetallePapeleta(Integer folio) {
		List<DetallePapeleta> detalle = detallePapeletaRepository.getDetallePapeleta(folio);
		return detalle;
	}
}
