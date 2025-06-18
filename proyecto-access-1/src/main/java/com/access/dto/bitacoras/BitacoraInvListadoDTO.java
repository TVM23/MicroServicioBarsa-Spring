package com.access.dto.bitacoras;

public class BitacoraInvListadoDTO {
	private Integer page;
    private Integer limit;
	private String fechaInicio;
    private String fechaFin;
    private Integer id;
    private String codigo;
    private String movimiento;
    private String descripcionMat;
    private String descripcionProd;
    private String aumenta;
    
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public String getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
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
	public String getMovimiento() {
		return movimiento;
	}
	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}
	public String getDescripcionMat() {
		return descripcionMat;
	}
	public void setDescripcionMat(String descripcionMat) {
		this.descripcionMat = descripcionMat;
	}
	public String getDescripcionProd() {
		return descripcionProd;
	}
	public void setDescripcionProd(String descripcionProd) {
		this.descripcionProd = descripcionProd;
	}
	public String getAumenta() {
		return aumenta;
	}
	public void setAumenta(String aumenta) {
		this.aumenta = aumenta;
	}
    

}
