package com.access.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.produccion.DesactivarDetencionDTO;
import com.access.dto.produccion.DetencionDTO;
import com.access.dto.produccion.FinalizarTiempoDTO;
import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.dto.produccion.PausarTiempoDTO;
import com.access.dto.produccion.ReiniciarTiempoDTO;
import com.access.dto.produccion.TiempoDTO;
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
	
	@KafkaListener(topics = "put-pausar-tiempo", groupId = "materia-service-group")
	public void pausarTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-pausarTiempo-response",
		           request -> {
		  	         PausarTiempoDTO dto = objectMapper.convertValue(request.get("data"), PausarTiempoDTO.class);
		             return produccionService.pausarTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "put-reiniciar-tiempo", groupId = "materia-service-group")
	public void reiniciarTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-reiniciarTiempo-response",
		           request -> {
		  	         ReiniciarTiempoDTO dto = objectMapper.convertValue(request.get("data"), ReiniciarTiempoDTO.class);
		             return produccionService.reiniciarTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "put-finalizar-tiempo", groupId = "materia-service-group")
	public void finalizarTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-finalizarTiempo-response",
		           request -> {
		  	         FinalizarTiempoDTO dto = objectMapper.convertValue(request.get("data"), FinalizarTiempoDTO.class);
		             return produccionService.finalizarTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "post-detencion-tiempo", groupId = "materia-service-group")
	public void detencionTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-detencion-response",
		           request -> {
		  	         DetencionDTO dto = objectMapper.convertValue(request.get("data"), DetencionDTO.class);
		             return produccionService.detencionTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "put-desactivar-detencion", groupId = "materia-service-group")
	public void desactivarDetencionTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-desactivarDetencion-response",
		           request -> {
		  	         DesactivarDetencionDTO dto = objectMapper.convertValue(request.get("data"), DesactivarDetencionDTO.class);
		             return produccionService.desactivarDetencionTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "get-registro-tiempo", groupId = "materia-service-group")
	public void obtenerTiempo(String message) {
		processKafkaMessage(
		           message,
		           "produccion-getTiempo-response",
		           request -> {
		  	         TiempoDTO dto = objectMapper.convertValue(request.get("data"), TiempoDTO.class);
		             return produccionService.obtenerTiempo(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "get-registro-detencion", groupId = "materia-service-group")
	public void obtenerDetencion(String message) {
		processKafkaMessage(
		           message,
		           "produccion-getDetencion-response",
		           request -> {
		  	         TiempoDTO dto = objectMapper.convertValue(request.get("data"), TiempoDTO.class);
		             return produccionService.obtenerDetencion(dto);
		           }
		     );
	}
	
	@KafkaListener(topics = "get-tiempos-folio", groupId = "materia-service-group")
	public void getTiemposByFolio(String message) {
		processKafkaMessage(
		           message,
		           "produccion-obtenerTiemposFolio-response",
		           request -> {
		  	         Integer procesoFolio = objectMapper.convertValue(request.get("data"), Integer.class);
		             return produccionService.getTiemposByFolio(procesoFolio);
		           }
		     );
	}
	
	@KafkaListener(topics = "get-ultima-detencion", groupId = "materia-service-group")
	public void getUltimaDetencioActiva(String message) {
		processKafkaMessage(
		           message,
		           "produccion-obtenerUltDetencion-response",
		           request -> {
		  	         Integer procesoFolio = objectMapper.convertValue(request.get("data"), Integer.class);
		             return produccionService.getUltimaDetencioActiva(procesoFolio);
		           }
		     );
	}
}
