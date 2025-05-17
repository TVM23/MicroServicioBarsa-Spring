package com.access.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseKafkaListener {
	 protected final KafkaTemplate<String, String> kafkaTemplate;
	 protected final ObjectMapper objectMapper = new ObjectMapper();

	 public BaseKafkaListener(KafkaTemplate<String, String> kafkaTemplate) {
	     this.kafkaTemplate = kafkaTemplate;
	 }
	 
	 	public <T> void processKafkaMessage(String message,  String responseTopic, Function<Map<String, Object>, T> serviceCall) {
	        try {
	        	ObjectMapper objectMapper = new ObjectMapper();
	            Map<String, Object> request = objectMapper.readValue(message, Map.class);
	            String correlationId = (String) request.get("correlationId");
	            
	            T data = serviceCall.apply(request);
	            
	           /*Object responseBody;
	            if (data instanceof ResponseEntity) {
	                responseBody = ((ResponseEntity<?>) data).getBody(); // ✅ solo mandamos el .body
	            } else {
	                responseBody = data;
	            }*/

	            Map<String, Object> response = new HashMap<>();
	            response.put("correlationId", correlationId);
	            response.put("data", data);
	            //response.put("data", responseBody);
	            kafkaTemplate.send(responseTopic, objectMapper.writeValueAsString(response));
	        } catch (Exception e) {
	            sendErrorResponse(responseTopic, message, "Error procesando la solicitud", 500, e);
	        }
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
