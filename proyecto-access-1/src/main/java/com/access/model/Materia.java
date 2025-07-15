package com.access.model;

import java.util.List;

public class Materia {
    
    private String codigoMat;
    private String Descripcion;
    private String Unidad;
    private Double PCompra;
    private Double Existencia;
    private Double Max;
    private Double Min;
    private Double InventarioInicial;
    private String UnidadEntrada;
    private Double CantXUnidad;
    private String Proceso;
    private Boolean Borrado;
    private Integer Merma;
    private List<Imagen> imagenes; 
    
    
	public Materia() {}
	
	
	public Integer getMerma() {
		return Merma;
	}
	public void setMerma(Integer merma) {
		Merma = merma;
	}
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
	public Double getExistencia() {
		return Existencia;
	}
	public void setExistencia(Double existencia) {
		Existencia = existencia;
	}
	public Double getMax() {
		return Max;
	}
	public void setMax(Double max) {
		Max = max;
	}
	public Double getMin() {
		return Min;
	}
	public void setMin(Double min) {
		Min = min;
	}
	public Double getInventarioInicial() {
		return InventarioInicial;
	}
	public void setInventarioInicial(Double inventarioInicial) {
		InventarioInicial = inventarioInicial;
	}
	public String getUnidadEntrada() {
		return UnidadEntrada;
	}
	public void setUnidadEntrada(String unidadEntrada) {
		UnidadEntrada = unidadEntrada;
	}
	public Double getCantXUnidad() {
		return CantXUnidad;
	}
	public void setCantXUnidad(Double cantXUnidad) {
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
	public List<Imagen> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<Imagen> imagenes) {
		this.imagenes = imagenes;
	}


    
    // Getters and Setters
}