package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.inventario.InventarioEntradaDTO;
import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.service.InventarioService;

@Service
public class InventarioListener extends BaseKafkaListener {
	private final InventarioService inventarioService;

    public InventarioListener(KafkaTemplate<String, String> kafkaTemplate, InventarioService inventarioService) {
        super(kafkaTemplate);
        this.inventarioService = inventarioService;
    }
    
    @KafkaListener(topics = "post-salida-crear", groupId = "materia-service-group")
    public void crearFichaSalida(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario_salidas-create-response", 
    			request -> {
    				InventarioSalidaDTO dto = objectMapper.convertValue(request.get("data"), InventarioSalidaDTO.class);
    				return inventarioService.createSalidaInventario(dto);
    			}
    		);
    }
    
    @KafkaListener(topics = "post-entrada-crear", groupId = "materia-service-group")
    public void creatFichaEntrada(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario_entradas-create-response", 
    			request -> {
    				InventarioEntradaDTO dto = objectMapper.convertValue(request.get("data"), InventarioEntradaDTO.class);
    				return inventarioService.createEntradaInventario(dto);
    			}
    		);
    }

}
