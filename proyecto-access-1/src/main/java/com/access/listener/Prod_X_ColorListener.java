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
import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.dto.producto.ProductoxColorPaginationDTO;
import com.access.model.Papeleta;
import com.access.model.Producto_X_Color;
import com.access.service.PapeletaService;
import com.access.service.Prod_x_ColorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Prod_X_ColorListener {
	
	private final Prod_x_ColorService prodXcolorService;
    private final KafkaTemplate<String, String> kafkaTemplate;
	 
    public Prod_X_ColorListener(KafkaTemplate<String, String> kafkaTemplate, Prod_x_ColorService prodXcolorService) {
        this.kafkaTemplate = kafkaTemplate;
        this.prodXcolorService = prodXcolorService;
    }
    
    
    @KafkaListener(topics = "get-prodXcolor-listado", groupId = "materia-service-group")
    public void getPapeletasFiltradas(String message) {
        try {
            // Parse message
        	ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            String correlationId = (String) request.get("correlationId");
            ProductoxColorPaginationDTO dto = objectMapper.convertValue(request.get("data"), ProductoxColorPaginationDTO.class);

            // Process request
            PaginationResult<List<Producto_X_Color>> paginatedList = prodXcolorService.getProdColorFiltrados(dto);

            // Send response back via Kafka
            Map<String, Object> response = new HashMap<>();
            response.put("correlationId", correlationId);
            response.put("data", paginatedList);

            kafkaTemplate.send("prodxcolor-pagination-response", objectMapper.writeValueAsString(response));
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
