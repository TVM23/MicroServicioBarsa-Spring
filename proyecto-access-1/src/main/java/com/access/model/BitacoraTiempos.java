package com.access.model;

public class BitacoraTiempos {
	private Integer Id;
	private String Fecha;
	private Integer Folio;
	private String Etapa;
	private String Movimiento;
	private String Usuario;
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getFecha() {
		return Fecha;
	}
	public void setFecha(String fecha) {
		Fecha = fecha;
	}
	public Integer getFolio() {
		return Folio;
	}
	public void setFolio(Integer folio) {
		Folio = folio;
	}
	public String getEtapa() {
		return Etapa;
	}
	public void setEtapa(String etapa) {
		Etapa = etapa;
	}
	public String getMovimiento() {
		return Movimiento;
	}
	public void setMovimiento(String movimiento) {
		Movimiento = movimiento;
	}
	public String getUsuario() {
		return Usuario;
	}
	public void setUsuario(String usuario) {
		Usuario = usuario;
	}
}
