package com.access.model;

import java.util.List;

public class InventarioSalida {
	private Integer id;
	private Integer folio;
	private String fecha;
	private String razon;
	private String destino;
	private String notas;
	private String usuario;
	private List<InventarioSalidaDetalle> detalle;
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
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getRazon() {
		return razon;
	}
	public void setRazon(String razon) {
		this.razon = razon;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
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
	public List<InventarioSalidaDetalle> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<InventarioSalidaDetalle> detalle) {
		this.detalle = detalle;
	}
	
}
