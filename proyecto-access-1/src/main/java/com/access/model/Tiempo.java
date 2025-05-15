package com.access.model;

public class Tiempo {
	private Integer id;
	private Integer procesoFolio;
	private String etapa;
	private Integer tiempo;
	private Long fechaInicio;
	private Long fechaFin;
	private Boolean isRunning;
	private Boolean isFinished;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProcesoFolio() {
		return procesoFolio;
	}
	public void setProcesoFolio(Integer procesoFolio) {
		this.procesoFolio = procesoFolio;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public Integer getTiempo() {
		return tiempo;
	}
	public void setTiempo(Integer tiempo) {
		this.tiempo = tiempo;
	}
	public Long getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Long fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public Long getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Long fechaFin) {
		this.fechaFin = fechaFin;
	}
	public Boolean getIsRunning() {
		return isRunning;
	}
	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}
	public Boolean getIsFinished() {
		return isFinished;
	}
	public void setIsFinished(Boolean isFinished) {
		this.isFinished = isFinished;
	}
	
}
