package com.access.dto.producto;

public class ProductoPaginationDTO {
	private Integer page;
	private Integer limit;
	private String codigo;
	private String descripcion;
	private String Unidad;
	private Double costo;
	private Double venta;
	private String ean;
	private String sku;
	private String tapices;
	private String borrado;
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUnidad() {
		return Unidad;
	}
	public void setUnidad(String unidad) {
		Unidad = unidad;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public Double getVenta() {
		return venta;
	}
	public void setVenta(Double venta) {
		this.venta = venta;
	}
	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getTapices() {
		return tapices;
	}
	public void setTapices(String tapices) {
		this.tapices = tapices;
	}
	public String getBorrado() {
		return borrado;
	}
	public void setBorrado(String borrado) {
		this.borrado = borrado;
	}
}
