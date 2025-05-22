package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.inventario.EntradasPaginationDTO;
import com.access.dto.inventario.InventarioEntradaDTO;
import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.SalidaPaginationDTO;
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
    				System.out.println("Antes del metodo");
    				return inventarioService.createEntradaInventario(dto);
    			}
    		);
    }
    
    @KafkaListener(topics = "get-inventario-entradas", groupId = "materia-service-group")
    public void getListadoEntrada(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario_entradas-pagination-response", 
    			request -> {
    				EntradasPaginationDTO dto = objectMapper.convertValue(request.get("data"), EntradasPaginationDTO.class);
    				return inventarioService.getListadoEntrada(dto);
    			}
    		);
    }
    
    @KafkaListener(topics = "get-inventario-salidas", groupId = "materia-service-group")
    public void getListadoSalida(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario_salidas-pagination-response", 
    			request -> {
    				SalidaPaginationDTO dto = objectMapper.convertValue(request.get("data"), SalidaPaginationDTO.class);
    				return inventarioService.getListadoSalida(dto);
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
    					System.err.println("Error al convertir DTO: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
    

}
