package com.access.model;

public class Notificacion {
	private Integer id;
	private String codigo;
	private String descripcion;
	private String mensaje;
	private Double minimo;
	private Double existencia;
	private String color;
	private String fecha;
	private String area;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Double getMinimo() {
		return minimo;
	}
	public void setMinimo(Double minimo) {
		this.minimo = minimo;
	}
	public Double getExistencia() {
		return existencia;
	}
	public void setExistencia(Double existencia) {
		this.existencia = existencia;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	
}
