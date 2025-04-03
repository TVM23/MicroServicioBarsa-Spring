package com.access.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.service.PapeletaService;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PapeletaListener {
	
	private final PapeletaService papeletaService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); 
	 
    public PapeletaListener(KafkaTemplate<String, String> kafkaTemplate, PapeletaService papeletaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.papeletaService = papeletaService;
    }
	
    @KafkaListener(topics = "get-papeleta-listado", groupId = "materia-service-group")
    public void getPapeletasFiltradas(String message) {
        processKafkaMessage(
                message,
                "papeleta-pagination-response",
                request -> {
                    PapeletaPaginationDTO dto = objectMapper.convertValue(request.get("data"), PapeletaPaginationDTO.class);
                    return papeletaService.getPapeletasFiltradas(dto);
                }
        );
    }
    
    @KafkaListener(topics = "get-papeleta-folio", groupId = "materia-service-group")
    public void getPapeletaCodigo(String message) {
        processKafkaMessage(
                message,
                "papeleta-codigo-response",
                request -> {
                    Integer folio = objectMapper.convertValue(request.get("data"), Integer.class);
                    return papeletaService.getPapeletasByFolio(folio);
                }
        );
    }
    
    // INICIAN METODOS QUE NO RECIBEN KAFKA PARA SE USAN PARA MANEJARLO
    
   //Metodo que maneja el proceso del mensaje, y el response
   public <T> void processKafkaMessage(String message,  String responseTopic, Function<Map<String, Object>, T> serviceCall) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            
            T data = serviceCall.apply(request);

            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", data);
            kafkaTemplate.send(responseTopic, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            sendErrorResponse(responseTopic, message, "Error procesando la solicitud", 500, e);
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
    
    // Método para enviar errores a Kafka
    private void sendErrorResponse(String topic, String correlationId, String errorMessage, int status, Exception e) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", errorMessage);
            errorResponse.put("status", status);

            kafkaTemplate.send(topic, objectMapper.writeValueAsString(errorResponse));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}