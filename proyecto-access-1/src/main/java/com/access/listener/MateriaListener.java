package com.access.listener;

import com.access.dto.materia.CreateMateriaDTO;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.service.MateriaService;

import java.util.Map;

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
		processKafkaMessage(message, "materia-pagination-response", request -> {
			try {
				MateriaPaginationDTO dto = objectMapper.convertValue(request.get("data"), MateriaPaginationDTO.class);
				return materiaService.getMateriasFiltradas(dto);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "get-materia-codigo", groupId = "materia-service-group")
	public void getMateriasCodigo(String message) {
		processKafkaMessage(message, "materia-codigo-response", request -> {
			try {
				String codigo = objectMapper.convertValue(request.get("data"), String.class);
				return materiaService.getMateriaByCodigo(codigo);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "post-materia-crear", groupId = "materia-service-group")
	public void createMateria(String message) {
		processKafkaMessage(message, "materia-create-response", request -> {
			try {
				Map<String, Object> data = (Map<String, Object>) request.get("data");
				CreateMateriaDTO dto = objectMapper.convertValue(data.get("createMateriaDto"), CreateMateriaDTO.class);
				String usuario = data.get("usuario").toString();
				return materiaService.createNewMateria(dto, usuario);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "put-materia-update", groupId = "materia-service-group")
	public void updateMateria(String message) {
		processKafkaMessage(message, "materia-update-response", request -> {
			try {
				Map<String, Object> data = (Map<String, Object>) request.get("data");
				CreateMateriaDTO dto = objectMapper.convertValue(data.get("updateMateriaDto"), CreateMateriaDTO.class);
				String usuario = data.get("usuario").toString();
				return materiaService.updateMateria(dto, usuario);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

	@KafkaListener(topics = "delete-materia-borrar", groupId = "materia-service-group")
	public void deleteMateria(String message) {
		processKafkaMessage(message, "materia-delete-response", request -> {
			try {
				Map<String, Object> data = (Map<String, Object>) request.get("data");
				String codigoMat = data.get("codigoMat").toString();
				String usuario = data.get("usuario").toString();
				return materiaService.deleteMateria(codigoMat, usuario);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

}