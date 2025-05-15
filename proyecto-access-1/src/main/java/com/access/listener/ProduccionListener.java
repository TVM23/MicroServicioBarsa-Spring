package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.service.ProduccionService;

@Service
public class ProduccionListener extends BaseKafkaListener {
	private final ProduccionService produccionService;
	
	public ProduccionListener(KafkaTemplate<String, String> kafkaTemplate, ProduccionService produccionService) {
        super(kafkaTemplate);
		this.produccionService = produccionService;
	}
	
	@KafkaListener(topics = "post-iniciar-tiempo", groupId = "materia-service-group")
	public void iniciarTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-iniciarTiempo-response",
		           request -> {
		  	         IniciarTiempoDTO dto = objectMapper.convertValue(request.get("data"), IniciarTiempoDTO.class);
		             return produccionService.iniciarTiempo(dto);
		           }
		     );
	}
}
