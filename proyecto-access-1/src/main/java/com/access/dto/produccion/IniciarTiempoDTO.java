package com.access.dto.produccion;

public class IniciarTiempoDTO {
	private Integer folio;
	private String etapa;
	private Long fechaInicio;
	
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public Long getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Long fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
}
