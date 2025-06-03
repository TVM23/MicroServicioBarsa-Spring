package com.access.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.access.model.Notificacion;
import com.access.service.NotificacionService;

@RestController
@RequestMapping("/notificacion")
public class NotificacionController {
	private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/lista-notificaciones")
    public ResponseEntity<List<Notificacion>> obtenerMateriaConExistenciaBaja() {
        List<Notificacion> bajas = notificacionService.evaluarYEnviarTodas();
        return ResponseEntity.ok(bajas);
    }

}
