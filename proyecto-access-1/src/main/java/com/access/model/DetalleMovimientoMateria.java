package com.access.model;

public class DetalleMovimientoMateria {
	private Integer id;
	private Integer consecutivo;
	private String codigoMat;
	private String descripcion;
	private Double cantidad;
	private Double existenciaAnterior;
	private Double pCosto;
	private Boolean procesada;
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getConsecutivo() {
		return consecutivo;
	}
	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}
	public String getCodigoMat() {
		return codigoMat;
	}
	public void setCodigoMat(String codigoMat) {
		this.codigoMat = codigoMat;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getExistenciaAnterior() {
		return existenciaAnterior;
	}
	public void setExistenciaAnterior(Double existenciaAnterior) {
		this.existenciaAnterior = existenciaAnterior;
	}
	public Double getpCosto() {
		return pCosto;
	}
	public void setpCosto(Double pCosto) {
		this.pCosto = pCosto;
	}
	public Boolean getProcesada() {
		return procesada;
	}
	public void setProcesada(Boolean procesada) {
		this.procesada = procesada;
	}
	
}
