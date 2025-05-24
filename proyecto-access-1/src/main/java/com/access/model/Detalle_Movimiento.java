package com.access.model;

public class Detalle_Movimiento {
	private Integer consecutivo;
	private String codigo;
	private String descProd;
	private String descColor;
	private Integer colorId;
	private Integer cantidad;
	private Integer noAlmacen;
	
	public String getDescProd() {
		return descProd;
	}
	public void setDescProd(String descProd) {
		this.descProd = descProd;
	}
	public String getDescColor() {
		return descColor;
	}
	public void setDescColor(String descColor) {
		this.descColor = descColor;
	}
	public Integer getConsecutivo() {
		return consecutivo;
	}
	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getColorId() {
		return colorId;
	}
	public void setColorId(Integer colorId) {
		this.colorId = colorId;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Integer getNoAlmacen() {
		return noAlmacen;
	}
	public void setNoAlmacen(Integer noAlmacen) {
		this.noAlmacen = noAlmacen;
	}
	
}
