package com.access.listener;

import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.service.PapeletaService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PapeletaListener extends BaseKafkaListener {
	
	private final PapeletaService papeletaService;
	 
    public PapeletaListener(KafkaTemplate<String, String> kafkaTemplate, PapeletaService papeletaService) {
        super(kafkaTemplate);
        this.papeletaService = papeletaService;
    }
	
    @KafkaListener(topics = "get-papeleta-listado", groupId = "materia-service-group")
    public void getPapeletasFiltradas(String message) {
        processKafkaMessage(
                message,
                "papeleta-pagination-response",
                request -> {
                    PapeletaPaginationDTO dto = objectMapper.convertValue(request.get("data"), PapeletaPaginationDTO.class);
                    return papeletaService.getPapeletasFiltradas(dto);
                }
        );
    }
    
    @KafkaListener(topics = "get-papeleta-folio", groupId = "materia-service-group")
    public void getPapeletaCodigo(String message) {
        processKafkaMessage(
                message,
                "papeleta-codigo-response",
                request -> {
                    Integer folio = objectMapper.convertValue(request.get("data"), Integer.class);
                    return papeletaService.getPapeletasByFolio(folio);
                }
        );
    }
    
}