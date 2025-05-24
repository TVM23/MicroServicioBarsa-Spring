package com.access.model;

public class Detencion {
	private Integer id;
	private Integer folio;
	private Integer tiempoId;
	private String etapa;
	private String motivo;
	private String fecha;
	private Boolean activa;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public Integer getTiempoId() {
		return tiempoId;
	}
	public void setTiempoId(Integer tiempoId) {
		this.tiempoId = tiempoId;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Boolean getActiva() {
		return activa;
	}
	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
	
}
