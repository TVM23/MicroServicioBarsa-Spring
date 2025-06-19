package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.bitacoras.BitacoraInvListadoDTO;
import com.access.service.BitacoraService;

@Service
public class BitacoraListener extends BaseKafkaListener  {
	private final BitacoraService bitacoraService; 
	
	public BitacoraListener(KafkaTemplate<String, String> kafkaTemplate, BitacoraService bitacoraService) {
        super(kafkaTemplate);
        this.bitacoraService = bitacoraService;
    }
	
	
	@KafkaListener(topics = "get-bitacora-inventario", groupId = "materia-service-group")
    public void getListadoBitacoraInv(String message) {
    	processKafkaMessage(
    			message, 
    			"bitacora-inventario-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					BitacoraInvListadoDTO dto = objectMapper.convertValue(data, BitacoraInvListadoDTO.class);
        				return bitacoraService.getListadoBitacoraInv(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }
}
