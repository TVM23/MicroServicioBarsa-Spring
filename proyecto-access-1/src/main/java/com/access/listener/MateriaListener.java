package com.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.access.dto.PaginationResult;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.model.Materia;
import com.access.model.Papeleta;
import com.access.service.MateriaService;
import com.access.service.PapeletaService;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MateriaListener {
	
	private final MateriaService materiaService;
    private final KafkaTemplate<String, String> kafkaTemplate;

	 
    public MateriaListener(KafkaTemplate<String, String> kafkaTemplate, MateriaService materiaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.materiaService = materiaService;
    }
	
    @KafkaListener(topics = "get-materia-listado", groupId = "materia-service-group")
    public void getAllMaterias(ConsumerRecord<String, String> record) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> request = objectMapper.readValue(record.value(), Map.class);
            String correlationId = request.get("correlationId");

            List<Materia> materias = materiaService.getAllMaterias();
            String response = objectMapper.writeValueAsString(Map.of("correlationId", correlationId, "data", materias));

            // Send response back to Kafka
            kafkaTemplate.send("materia-response", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @KafkaListener(topics = "get-materia-listado-filtro", groupId = "materia-service-group")
    public void getMateriasFiltradas(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            MateriaPaginationDTO dto = objectMapper.convertValue(request.get("data"), MateriaPaginationDTO.class);

            // Process request
            PaginationResult<List<Materia>> paginatedList = materiaService.getMateriasFiltradas(dto);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", paginatedList);

            kafkaTemplate.send("materia-pagination-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @KafkaListener(topics = "get-materia-codigo", groupId = "materia-service-group")
    public void getMateriasCodigo(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            
            String codigo = objectMapper.convertValue(request.get("data"), String.class);
            List<Materia> materia = materiaService.getMateriasByCodigoMat(codigo);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", materia);

            kafkaTemplate.send("materia-codigo-response", objectMapper.writeValueAsString(response));
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
