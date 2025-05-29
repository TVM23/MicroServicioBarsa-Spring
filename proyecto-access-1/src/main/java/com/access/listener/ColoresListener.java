package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.colores.ColoresPaginationDTO;
import com.access.dto.colores.CreateColorDTO;
import com.access.service.ColoresService;

@Service
public class ColoresListener extends BaseKafkaListener {

	private final ColoresService coloresService;

	public ColoresListener(KafkaTemplate<String, String> kafkaTemplate, ColoresService coloresService) {
		super(kafkaTemplate);
		this.coloresService = coloresService;
	}

	@KafkaListener(topics = "get-colores-listado", groupId = "materia-service-group")
	public void getColoresFiltrado(String message) {
		processKafkaMessage(message, "colores-pagination-response", request -> {
			try {
				Object data = request.get("data");
				ColoresPaginationDTO dto = objectMapper.convertValue(data, ColoresPaginationDTO.class);
				return coloresService.getColoresFiltrados(dto);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "get-colorId-codigo", groupId = "materia-service-group")
	public void getColorByCodigo(String message) {
		processKafkaMessage(message, "colores-colorId-response", request -> {
			try {
				Integer colorId = objectMapper.convertValue(request.get("data"), Integer.class);
				return coloresService.getColorByCodigo(colorId);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "post-color-crear", groupId = "materia-service-group")
	public void createColor(String message) {
		processKafkaMessage(message, "colores-create-response", request -> {
			try {
				CreateColorDTO dto = objectMapper.convertValue(request.get("data"), CreateColorDTO.class);
				return coloresService.createNewColor(dto);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}
}
