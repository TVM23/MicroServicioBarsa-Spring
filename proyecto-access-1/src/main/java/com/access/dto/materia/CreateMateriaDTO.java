package com.access.dto.materia;

import java.util.List;

import com.access.dto.ImagenDTO;

public class CreateMateriaDTO {
	private String codigoMat;
    private String descripcion;
    private String unidad;
    private Double pcompra;
    private Double existencia;
    private Double max;
    private Double min;
    private Double inventarioInicial;
    private String unidadEntrada;
    private Double cantxunidad;
    private String proceso;
    private String borrado;
    private Integer merma;
    private List<ImagenDTO> imagenes; 
    
    
	public Integer getMerma() {
		return merma;
	}
	public void setMerma(Integer merma) {
		this.merma = merma;
	}
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
	public Double getExistencia() {
		return existencia;
	}
	public void setExistencia(Double existencia) {
		this.existencia = existencia;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getInventarioInicial() {
		return inventarioInicial;
	}
	public void setInventarioInicial(Double inventarioInicial) {
		this.inventarioInicial = inventarioInicial;
	}
	public String getUnidadEntrada() {
		return unidadEntrada;
	}
	public void setUnidadEntrada(String unidadEntrada) {
		this.unidadEntrada = unidadEntrada;
	}
	public Double getCantxunidad() {
		return cantxunidad;
	}
	public void setCantxunidad(Double cantxunidad) {
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
