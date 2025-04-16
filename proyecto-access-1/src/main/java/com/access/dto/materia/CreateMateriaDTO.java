package com.access.dto.materia;

import java.util.List;

import com.access.dto.ImagenDTO;

public class CreateMateriaDTO {
	private String codigoMat;
    private String descripcion;
    private String unidad;
    private Double pcompra;
    private Integer existencia;
    private Integer max;
    private Integer min;
    private Integer inventarioInicial;
    private String unidadEntrada;
    private Integer cantxunidad;
    private String proceso;
    private String borrado;
    private List<ImagenDTO> imagenes; 
    
    
	public List<ImagenDTO> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<ImagenDTO> imagenes) {
		this.imagenes = imagenes;
	}
	public String getCodigoMat() {
		return codigoMat;
	}
	public void setCodigoMat(String codigoMat) {
		this.codigoMat = codigoMat;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	public Double getPcompra() {
		return pcompra;
	}
	public void setPcompra(Double pcompra) {
		this.pcompra = pcompra;
	}
	public Integer getExistencia() {
		return existencia;
	}
	public void setExistencia(Integer existencia) {
		this.existencia = existencia;
	}
	public Integer getMax() {
		return max;
	}
	public void setMax(Integer max) {
		this.max = max;
	}
	public Integer getMin() {
		return min;
	}
	public void setMin(Integer min) {
		this.min = min;
	}
	public Integer getInventarioInicial() {
		return inventarioInicial;
	}
	public void setInventarioInicial(Integer inventarioInicial) {
		this.inventarioInicial = inventarioInicial;
	}
	public String getUnidadEntrada() {
		return unidadEntrada;
	}
	public void setUnidadEntrada(String unidadEntrada) {
		this.unidadEntrada = unidadEntrada;
	}
	public Integer getCantxunidad() {
		return cantxunidad;
	}
	public void setCantxunidad(Integer cantxunidad) {
		this.cantxunidad = cantxunidad;
	}
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getBorrado() {
		return borrado;
	}
	public void setBorrado(String borrado) {
		this.borrado = borrado;
	}
    
    
}
