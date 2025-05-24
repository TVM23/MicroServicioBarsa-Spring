package com.access.dto.inventario;

import java.util.List;

public class MovimientosDTO {
	private Integer movId;
	private String fecha;
	private Integer folio;
	private String usuario;
	private List<DetalleMovProductoDTO> detalles;

	public Integer getMovId() {
		return movId;
	}
	public void setMovId(Integer movId) {
		this.movId = movId;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public List<DetalleMovProductoDTO> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<DetalleMovProductoDTO> detalles) {
		this.detalles = detalles;
	}
	
}
