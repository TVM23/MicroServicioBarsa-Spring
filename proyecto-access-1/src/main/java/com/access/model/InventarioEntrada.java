package com.access.model;

import java.util.List;

public class InventarioEntrada {
	private Integer id;
	private Integer proveedorId;
	private String fecha;
	private Double montoTotal;
	private String notas;
	private String usuario;
	private List<InventarioEntradaDetalle> detalle;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProveedorId() {
		return proveedorId;
	}
	public void setProveedorId(Integer proveedorId) {
		this.proveedorId = proveedorId;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Double getMontoTotal() {
		return montoTotal;
	}
	public void setMontoTotal(Double montoTotal) {
		this.montoTotal = montoTotal;
	}
	public String getNotas() {
		return notas;
	}
	public void setNotas(String notas) {
		this.notas = notas;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public List<InventarioEntradaDetalle> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<InventarioEntradaDetalle> detalle) {
		this.detalle = detalle;
	}
	
}
