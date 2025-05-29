package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.service.NotificacionService;

@Service
public class NotificacionListener extends BaseKafkaListener {

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
