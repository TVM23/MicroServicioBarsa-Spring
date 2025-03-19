package com.access.listener;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoPaginationDTO;
import com.access.model.Producto;
import com.access.service.ProductoService;
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
            ProducerRecord<String, String> record =new ProducerRecord<String, String>("producto-pagination-response",
            		null,
            		null,
            		objectMapper.writeValueAsString(response),
            		List.of(new RecordHeader("custom-header", "header-value".getBytes(StandardCharsets.UTF_8)))); 

            kafkaTemplate.send(record);
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
