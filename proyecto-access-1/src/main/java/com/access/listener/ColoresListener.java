package com.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.colores.ColoresPaginationDTO;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.model.Colores;
import com.access.model.Papeleta;
import com.access.service.ColoresService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ColoresListener {
	
	private final ColoresService coloresService;
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	public ColoresListener(KafkaTemplate<String, String> kafkaTemplate, ColoresService coloresService) {
		this.kafkaTemplate = kafkaTemplate;
		this.coloresService = coloresService;
	}
	
	@KafkaListener(topics = "get-colores-listado", groupId = "materia-service-group")
	public void gettColoresFiltrado(String message) {
		 try {
	            // Parse message
	        	ObjectMapper objectMapper = new ObjectMapper();
	            Map<String, Object> request = objectMapper.readValue(message, Map.class);
	            String correlationId = (String) request.get("correlationId");
	            ColoresPaginationDTO dto = objectMapper.convertValue(request.get("data"), ColoresPaginationDTO.class);

	            // Process request
	            PaginationResult<List<Colores>> paginatedList = coloresService.getColoresFiltrados(dto);

	            // Send response back via Kafka
	            Map<String, Object> response = new HashMap<>();
	            response.put("correlationId", correlationId);
	            response.put("data", paginatedList);

	            kafkaTemplate.send("colores-pagination-response", objectMapper.writeValueAsString(response));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

}
