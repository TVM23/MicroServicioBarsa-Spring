package com.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoPaginationDTO;
import com.access.model.Producto;
import com.access.service.ProductoService;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductoListener {
	
	private final ProductoService productoService;
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	public ProductoListener(KafkaTemplate<String, String> kafkaTemplate, ProductoService productoService) {
		this.kafkaTemplate = kafkaTemplate;
		this.productoService = productoService;
	}
	
	@KafkaListener(topics = "get-producto-listado", groupId = "materia-service-group")
	public void getProductosFiltrados(String message) {
		try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            ProductoPaginationDTO dto = objectMapper.convertValue(request.get("data"), ProductoPaginationDTO.class);

            // Process request
            PaginationResult<List<Producto>> paginatedList = productoService.getProductosFiltrados(dto);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", paginatedList);
            kafkaTemplate.send("producto-pagination-response", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@KafkaListener(topics = "get-producto-codigo",  groupId = "materia-service-group")
	public void getProductoPorCodigo(String message) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            String codigo = objectMapper.convertValue(request.get("data"), String.class);
            
            List<Producto> producto = productoService.getProductoCodigo(codigo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", producto);
            kafkaTemplate.send("producto-codigo-response", objectMapper.writeValueAsString(response));
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