package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.papeleta.PapeletaPaginationDTO;
import com.access.dto.producto.ProductoxColorPaginationDTO;
import com.access.service.Prod_x_ColorService;

@Service
public class Prod_X_ColorListener extends BaseKafkaListener {

	private final Prod_x_ColorService prodXcolorService;

	public Prod_X_ColorListener(KafkaTemplate<String, String> kafkaTemplate, Prod_x_ColorService prodXcolorService) {
		super(kafkaTemplate);
		this.prodXcolorService = prodXcolorService;
	}

	@KafkaListener(topics = "get-prodXcolor-listado", groupId = "materia-service-group")
	public void getPapeletasFiltradas(String message) {
		processKafkaMessage(message, "prodxcolor-pagination-response", request -> {
			try {
				ProductoxColorPaginationDTO dto = objectMapper.convertValue(request.get("data"),
						ProductoxColorPaginationDTO.class);
				return prodXcolorService.getProdColorFiltrados(dto);
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}

}