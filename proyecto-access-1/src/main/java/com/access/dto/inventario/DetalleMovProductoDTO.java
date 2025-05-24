package com.access.dto.inventario;

public class DetalleMovProductoDTO {
	private String codigo;
	private Integer colorId;
	private Integer cantidad;
	private Integer noAlmacen;
	
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
