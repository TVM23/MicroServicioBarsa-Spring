package com.access.model;

import java.util.List;

public class Tiempo {
	private Integer id;
	private Integer procesoFolio;
	private String etapa;
	private Integer tiempo;
	private String fechaInicio;
	private String fechaFin;
	private Boolean isRunning;
	private Boolean isFinished;
	private String usuario;
    private List<Detencion> detenciones;
	
	public List<Detencion> getDetenciones() {
		return detenciones;
	}
	public void setDetenciones(List<Detencion> detenciones) {
		this.detenciones = detenciones;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
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
	public String getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public String getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(String fechaFin) {
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
