package com.access.listener;

import com.access.dto.producto.ProductoPaginationDTO;
import com.access.service.ProductoService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductoListener extends BaseKafkaListener {
	
	private final ProductoService productoService;
	
	public ProductoListener(KafkaTemplate<String, String> kafkaTemplate, ProductoService productoService) {
        super(kafkaTemplate);
		this.productoService = productoService;
	}
	
	@KafkaListener(topics = "get-producto-listado", groupId = "materia-service-group")
	public void getProductosFiltrados(String message) {
		processKafkaMessage(
                message,
                "prodxcolor-pagination-response",
                request -> {
                    try {
                    	ProductoPaginationDTO dto = objectMapper.convertValue(request.get("data"), ProductoPaginationDTO.class);
                        return productoService.getProductosFiltrados(dto);
        			} catch (Exception e) {
        				System.err.println("Error en el servicio: " + e.getMessage());
        				e.printStackTrace();
        				throw e;
        			}
                }
        );
	}
	
	@KafkaListener(topics = "get-producto-codigo",  groupId = "materia-service-group")
	public void getProductoPorCodigo(String message) {
		processKafkaMessage(
                message,
                "prodxcolor-pagination-response",
                request -> {
                    try {
                    	String codigo = objectMapper.convertValue(request.get("data"), String.class);
                        return productoService.getProductoCodigo(codigo);
        			} catch (Exception e) {
        				System.err.println("Error en el servicio: " + e.getMessage());
        				e.printStackTrace();
        				throw e;
        			}
                }
        );
	}

}