package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.MovimientosDTO;
import com.access.dto.inventario.MovimientoMateriaPagiDto;
import com.access.dto.inventario.MovimientoProductoPagiDTO;
import com.access.service.InventarioService;

@Service
public class InventarioListener extends BaseKafkaListener {
	private final InventarioService inventarioService;

    public InventarioListener(KafkaTemplate<String, String> kafkaTemplate, InventarioService inventarioService) {
        super(kafkaTemplate);
        this.inventarioService = inventarioService;
    }
    
    @KafkaListener(topics = "get-movimiento-materia", groupId = "materia-service-group")
    public void getListadoMovMateria(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario-movimiento_materia-pagination-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					MovimientoMateriaPagiDto dto = objectMapper.convertValue(data, MovimientoMateriaPagiDto.class);
        				return inventarioService.getListadoMovMateria(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
    
    @KafkaListener(topics = "get-movimiento-producto", groupId = "materia-service-group")
    public void getListadoMovProducto(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario-movimiento_producto-pagination-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					MovimientoProductoPagiDTO dto = objectMapper.convertValue(data, MovimientoProductoPagiDTO.class);
        				return inventarioService.getListadoMovProducto(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
    
    
    @KafkaListener(topics = "post-movimiento-materia", groupId = "materia-service-group")
    public void createMovimientoMateria(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario-movimiento_materia-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					MovimientoMateriaDTO dto = objectMapper.convertValue(data, MovimientoMateriaDTO.class);
    					return inventarioService.createMovimientoMateria(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
    
    @KafkaListener(topics = "post-movimiento-producto", groupId = "materia-service-group")
    public void createMovimientoProducto(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario-movimiento_producto-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					MovimientosDTO dto = objectMapper.convertValue(data, MovimientosDTO.class);
    					return inventarioService.createMovimientoProducto(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
    

}
