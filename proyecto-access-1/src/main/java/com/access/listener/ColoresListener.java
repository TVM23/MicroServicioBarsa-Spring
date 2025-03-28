package com.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
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
	
	@KafkaListener(topics = "get-papeleta-codigo", groupId = "materia-service-group")
    public void getColorByCodigo(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            
            Integer colorId = objectMapper.convertValue(request.get("data"), Integer.class);
            List<Colores> color = coloresService.getColorByCodigo(colorId);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", color);

            kafkaTemplate.send("colores-colorId-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 // Configurar Kafka para evitar desconexión
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setIdleBetweenPolls(30000); // 30s entre polls
        return factory;
    }
    
    // Configuración personalizada de Kafka
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);  // 1 min timeout
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 15000);  // Heartbeat cada 15s
        return props;
    }

}
