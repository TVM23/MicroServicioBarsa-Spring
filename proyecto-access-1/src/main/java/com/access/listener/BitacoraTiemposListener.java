package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.bitacoras.BitacoraProdListadoDTO;
import com.access.service.BitacoraTiempoService;

@Service
public class BitacoraTiemposListener extends BaseKafkaListener  {
	private final BitacoraTiempoService bitacoraTiemposService; 
	
	public BitacoraTiemposListener(KafkaTemplate<String, String> kafkaTemplate, BitacoraTiempoService bitacoraTiemposService) {
        super(kafkaTemplate);
        this.bitacoraTiemposService = bitacoraTiemposService;
    }
	
	
	@KafkaListener(topics = "get-bitacora-produccion", groupId = "materia-service-group")
    public void getListadoBitacoraProd(String message) {
    	processKafkaMessage(
    			message, 
    			"bitacora-produccion-response", 
    			request -> {
    				try {
    					Object data = request.get("data");
    					BitacoraProdListadoDTO dto = objectMapper.convertValue(data, BitacoraProdListadoDTO.class);
        				return bitacoraTiemposService.getListadoBitacoraProd(dto);
    				} catch (Exception e) {
    					System.err.println("Error en el servicio: " + e.getMessage());
    					e.printStackTrace();
    					throw e;
    				}
    			}
    		);
    }

}
