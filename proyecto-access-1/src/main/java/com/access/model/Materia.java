package com.access.model;


public class Materia {
    
    private String codigoMat;
    private String Descripcion;
    private String Unidad;
    private Double PCompra;
    private Integer Existencia;
    private Integer Max;
    private Integer Min;
    private Integer InventarioInicial;
    private String UnidadEntrada;
    private Integer CantXUnidad;
    private String Proceso;
    private Boolean Borrado;

    
	public String getCodigoMat() {
		return codigoMat;
	}
	public void setCodigoMat(String codigoMat) {
		this.codigoMat = codigoMat;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.Descripcion = descripcion;
	}
	public String getUnidad() {
		return Unidad;
	}
	public void setUnidad(String unidad) {
		this.Unidad = unidad;
	}
	public Double getPCompra() {
		return PCompra;
	}
	public void setPCompra(Double pCompra) {
		PCompra = pCompra;
	}
	public Integer getExistencia() {
		return Existencia;
	}
	public void setExistencia(Integer existencia) {
		Existencia = existencia;
	}
	public Integer getMax() {
		return Max;
	}
	public void setMax(Integer max) {
		Max = max;
	}
	public Integer getMin() {
		return Min;
	}
	public void setMin(Integer min) {
		Min = min;
	}
	public Integer getInventarioInicial() {
		return InventarioInicial;
	}
	public void setInventarioInicial(Integer inventarioInicial) {
		InventarioInicial = inventarioInicial;
	}
	public String getUnidadEntrada() {
		return UnidadEntrada;
	}
	public void setUnidadEntrada(String unidadEntrada) {
		UnidadEntrada = unidadEntrada;
	}
	public Integer getCantXUnidad() {
		return CantXUnidad;
	}
	public void setCantXUnidad(Integer cantXUnidad) {
		CantXUnidad = cantXUnidad;
	}
	public String getProceso() {
		return Proceso;
	}
	public void setProceso(String proceso) {
		Proceso = proceso;
	}
	public Boolean getBorrado() {
		return Borrado;
	}
	public void setBorrado(Boolean borrado) {
		Borrado = borrado;
	}
	

    
    // Getters and Setters
}