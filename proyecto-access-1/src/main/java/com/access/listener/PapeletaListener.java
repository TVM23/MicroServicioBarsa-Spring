package com.access.listener;

import com.access.dto.materia.MateriaPaginationDTO;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.service.PapeletaService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PapeletaListener extends BaseKafkaListener {

	private final PapeletaService papeletaService;

	public PapeletaListener(KafkaTemplate<String, String> kafkaTemplate, PapeletaService papeletaService) {
		super(kafkaTemplate);
		this.papeletaService = papeletaService;
	}

	@KafkaListener(topics = "get-papeleta-listado", groupId = "materia-service-group")
	public void getPapeletasFiltradas(String message) {
		processKafkaMessage(message, "papeleta-pagination-response", request -> {
			try {
				PapeletaPaginationDTO dto = objectMapper.convertValue(request.get("data"), PapeletaPaginationDTO.class);
				return papeletaService.getPapeletasFiltradas(dto);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "get-papeleta-folio", groupId = "materia-service-group")
	public void getPapeletaCodigo(String message) {
		processKafkaMessage(message, "papeleta-codigo-response", request -> {
			try {
				Integer folio = objectMapper.convertValue(request.get("data"), Integer.class);
				return papeletaService.getPapeletasByFolio(folio);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

}