package com.access.dto.materia;

import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

public class MateriaPaginationDTO {

    private int page;
    private int limit;
    private String codigoMat;
    private String descripcion;
    private String unidad;
    private String proceso;
    private Boolean borrado;
    
    public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
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
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		proceso = proceso;
	}
	public Boolean getBorrado() {
		return borrado;
	}
	public void setBorrado(Boolean borrado) {
		borrado = borrado;
	}
}
