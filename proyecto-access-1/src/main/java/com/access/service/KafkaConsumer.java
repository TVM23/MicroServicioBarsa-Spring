package com.access.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.access.model.Materia;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Service
public class KafkaConsumer {
	
	private final MateriaService materiaService;
    private final KafkaTemplate<String, String> kafkaTemplate;
	 
    public KafkaConsumer(KafkaTemplate<String, String> kafkaTemplate, MateriaService materiaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.materiaService = materiaService;
    }
	
    @KafkaListener(topics = "access-api-topic", groupId = "access-api-id")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received Message: " + record.value());
    }
    

    @KafkaListener(topics = "get-listado-materia", groupId = "access-api-id")
    public void listen1(ConsumerRecord<String, String> record) {
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
    
    @KafkaListener(topics = "get-listado-materia-filtro", groupId = "access-api-id")
    public void listen(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            MateriaPaginationDTO dto = objectMapper.convertValue(request.get("data"), MateriaPaginationDTO.class);

            // Process request
            List<Materia> materias = materiaService.getMateriasFiltradas(dto);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", materias);

            kafkaTemplate.send("materia-pagination-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
