package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.model.Notificacion;
import com.access.service.NotificacionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class NotificacionListener extends BaseKafkaListener{

	private final NotificacionService notificacionService;

	public NotificacionListener(KafkaTemplate<String, String> kafkaTemplate, NotificacionService notificacionService) {
		super(kafkaTemplate);
		this.notificacionService = notificacionService;
	}

	@KafkaListener(topics = "get-notificaciones", groupId = "materia-service-group")
	public void getNotificaciones(String message) {
		processKafkaMessage(message, "notificaciones-response", request -> {
			try {
				Object data = request.get("data");
				return notificacionService.getListadoNotificaciones();
			} catch (Exception e) {
				System.err.println("Error en el servicio: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		});
	}
	
}
