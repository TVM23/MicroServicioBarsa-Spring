package com.access.model;

public class Bitacora {
	private Integer Id;
	private String Fecha;
	private String Codigo;
	private String DescripcionCod;
	private String Movimiento;
	private Boolean Aumenta;
	private Double Cantidad;
	private Integer NoAlmacen;
	private Double ExistAnt;
	private Double ExistNva;
	private Integer ColorId;
	private String DescripcionColor;
	
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
	public String getCodigo() {
		return Codigo;
	}
	public void setCodigo(String codigo) {
		Codigo = codigo;
	}
	public String getDescripcionCod() {
		return DescripcionCod;
	}
	public void setDescripcionCod(String descripcionCod) {
		DescripcionCod = descripcionCod;
	}
	public String getMovimiento() {
		return Movimiento;
	}
	public void setMovimiento(String movimiento) {
		Movimiento = movimiento;
	}
	public Boolean getAumenta() {
		return Aumenta;
	}
	public void setAumenta(Boolean aumenta) {
		Aumenta = aumenta;
	}
	public Double getCantidad() {
		return Cantidad;
	}
	public void setCantidad(Double cantidad) {
		Cantidad = cantidad;
	}
	public Integer getNoAlmacen() {
		return NoAlmacen;
	}
	public void setNoAlmacen(Integer noAlmacen) {
		NoAlmacen = noAlmacen;
	}
	public Double getExistAnt() {
		return ExistAnt;
	}
	public void setExistAnt(Double existAnt) {
		ExistAnt = existAnt;
	}
	public Double getExistNva() {
		return ExistNva;
	}
	public void setExistNva(Double existNva) {
		ExistNva = existNva;
	}
	public Integer getColorId() {
		return ColorId;
	}
	public void setColorId(Integer colorId) {
		ColorId = colorId;
	}
	public String getDescripcionColor() {
		return DescripcionColor;
	}
	public void setDescripcionColor(String descripcionColor) {
		this.DescripcionColor = descripcionColor;
	}
	
	
}
