package com.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.access.dto.PaginationResult;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.model.Papeleta;
import com.access.service.PapeletaService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PapeletaListener {
	
	private final PapeletaService papeletaService;
    private final KafkaTemplate<String, String> kafkaTemplate;
	 
    public PapeletaListener(KafkaTemplate<String, String> kafkaTemplate, PapeletaService papeletaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.papeletaService = papeletaService;
    }
	
    
    @KafkaListener(topics = "get-papeleta-listado", groupId = "materia-service-group")
    public void getPapeletasFiltradas(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            PapeletaPaginationDTO dto = objectMapper.convertValue(request.get("data"), PapeletaPaginationDTO.class);

            // Process request
            PaginationResult<List<Papeleta>> paginatedList = papeletaService.getPapeletasFiltradas(dto);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", paginatedList);

            kafkaTemplate.send("papeleta-pagination-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @KafkaListener(topics = "get-papeleta-folio", groupId = "materia-service-group")
    public void getPapeletaCodigo(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            
            Integer folio = objectMapper.convertValue(request.get("data"), Integer.class);
            List<Papeleta> materia = papeletaService.getPapeletasByFolio(folio);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", materia);

            kafkaTemplate.send("papeleta-codigo-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
