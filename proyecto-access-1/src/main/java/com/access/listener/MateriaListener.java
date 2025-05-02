package com.access.listener;

import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.dto.materia.CreateMateriaDTO;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.service.MateriaService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MateriaListener extends BaseKafkaListener {
	
	private final MateriaService materiaService;

    public MateriaListener(KafkaTemplate<String, String> kafkaTemplate, MateriaService materiaService) {
        super(kafkaTemplate);
        this.materiaService = materiaService;
    }
    
    @KafkaListener(topics = "get-materia-listado-filtro", groupId = "materia-service-group")
    public void getMateriasFiltradas(String message) {
        processKafkaMessage(
 	           message,
 	          "materia-pagination-response",
 	           request -> {
 	              MateriaPaginationDTO dto = objectMapper.convertValue(request.get("data"), MateriaPaginationDTO.class);
 	             return materiaService.getMateriasFiltradas(dto);
 	           }
 	    );
    }
    
    @KafkaListener(topics = "get-materia-codigo", groupId = "materia-service-group")
    public void getMateriasCodigo(String message) {
        processKafkaMessage(
  	           message,
  	          "materia-codigo-response",
  	           request -> {
  	             String codigo = objectMapper.convertValue(request.get("data"), String.class);
  	             return materiaService.getMateriaByCodigo(codigo);
  	           }
  	    );
    } 
    
    @KafkaListener(topics = "post-materia-crear", groupId = "materia-service-group")
    public void createMateria(String message) {
    	processKafkaMessage(
    			message, 
    			"materia-create-response", 
    			request -> {
    				CreateMateriaDTO dto = objectMapper.convertValue(request.get("data"), CreateMateriaDTO.class);
    				return materiaService.createNewMateria(dto);
    			}
    		);
    }
    
    @KafkaListener(topics = "put-materia-update", groupId = "materia-service-group")
    public void updateMateria(String message) {
    	processKafkaMessage(
    			message, 
    			"materia-update-response", 
    			request -> {
    				CreateMateriaDTO dto = objectMapper.convertValue(request.get("data"), CreateMateriaDTO.class);
    				return materiaService.updateMateria(dto);
    			}
    		);
    }
    
    @KafkaListener(topics = "delete-materia-borrar", groupId = "materia-service-group")
    public void deleteMateria(String message) {
    	processKafkaMessage(
    			message, 
    			"materia-delete-response", 
    			request -> {
    				String codigo = objectMapper.convertValue(request.get("data"), String.class);
    				return materiaService.deleteMateria(codigo);
    			}
    		);
    }
    
    @KafkaListener(topics = "post-salida-crear", groupId = "materia-service-group")
    public void creatFicha(String message) {
    	processKafkaMessage(
    			message, 
    			"inventario_salidas-create-response", 
    			request -> {
    				InventarioSalidaDTO dto = objectMapper.convertValue(request.get("data"), InventarioSalidaDTO.class);
    				return materiaService.createSalidaInventario(dto);
    			}
    		);
    }
    
}