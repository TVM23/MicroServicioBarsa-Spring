package com.access.model;

public class Producto {
	private String Codigo;
	private String Descripcion;
	private String Unidad;
	private Double Costo;
	private Double Venta;
	private Integer Existencia;
	private Integer InventarioInicial;
	private Double Sueldo;
	private Double Prestaciones;
	private Double Aportaciones;
	private String Documento;
	private String EAN;
	private String SKU;
	private Boolean Tapices;
	private Boolean Borrado;
	
	
	public String getCodigo() {
		return Codigo;
	}
	public void setCodigo(String codigo) {
		Codigo = codigo;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getUnidad() {
		return Unidad;
	}
	public void setUnidad(String unidad) {
		Unidad = unidad;
	}
	public Double getCosto() {
		return Costo;
	}
	public void setCosto(Double costo) {
		Costo = costo;
	}
	public Double getVenta() {
		return Venta;
	}
	public void setVenta(Double venta) {
		Venta = venta;
	}
	public Integer getExistencia() {
		return Existencia;
	}
	public void setExistencia(Integer existencia) {
		Existencia = existencia;
	}
	public Integer getInventarioInicial() {
		return InventarioInicial;
	}
	public void setInventarioInicial(Integer inventarioInicial) {
		InventarioInicial = inventarioInicial;
	}
	public Double getSueldo() {
		return Sueldo;
	}
	public void setSueldo(Double sueldo) {
		Sueldo = sueldo;
	}
	public Double getPrestaciones() {
		return Prestaciones;
	}
	public void setPrestaciones(Double prestaciones) {
		Prestaciones = prestaciones;
	}
	public Double getAportaciones() {
		return Aportaciones;
	}
	public void setAportaciones(Double aportaciones) {
		Aportaciones = aportaciones;
	}
	public String getDocumento() {
		return Documento;
	}
	public void setDocumento(String documento) {
		Documento = documento;
	}
	public String getEAN() {
		return EAN;
	}
	public void setEAN(String eAN) {
		EAN = eAN;
	}
	public String getSKU() {
		return SKU;
	}
	public void setSKU(String sKU) {
		SKU = sKU;
	}
	public Boolean getTapices() {
		return Tapices;
	}
	public void setTapices(Boolean tapices) {
		Tapices = tapices;
	}
	public Boolean getBorrado() {
		return Borrado;
	}
	public void setBorrado(Boolean borrado) {
		Borrado = borrado;
	}
	
}
