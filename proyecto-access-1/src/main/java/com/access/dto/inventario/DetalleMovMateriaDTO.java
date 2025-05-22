package com.access.dto.inventario;

public class DetalleMovMateriaDTO {
	private String codigoMat;
    private Double cantidad;
    private Boolean procesada;
    
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
	public Boolean getProcesada() {
		return procesada;
	}
	public void setProcesada(Boolean procesada) {
		this.procesada = procesada;
	}
    
}
