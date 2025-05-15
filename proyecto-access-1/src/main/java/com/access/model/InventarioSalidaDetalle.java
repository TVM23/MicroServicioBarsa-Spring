package com.access.model;

public class InventarioSalidaDetalle {
	private Integer id;
	private String codigoMat;
	private Double cantidad;
	private Integer id_Salida;
	private String descripcion;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getId_Salida() {
		return id_Salida;
	}
	public void setId_Salida(Integer id_Salida) {
		this.id_Salida = id_Salida;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
