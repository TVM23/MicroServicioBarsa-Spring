package com.access.model;

public class InventarioEntradaDetalle {
	private Integer id;
	private String codigoMat;
	private Double cantidad;
	private Double PCompra;
	private Integer id_Entrada;
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
	public Double getPCompra() {
		return PCompra;
	}
	public void setPCompra(Double pCompra) {
		PCompra = pCompra;
	}
	public Integer getId_Entrada() {
		return id_Entrada;
	}
	public void setId_Entrada(Integer id_Entrada) {
		this.id_Entrada = id_Entrada;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
