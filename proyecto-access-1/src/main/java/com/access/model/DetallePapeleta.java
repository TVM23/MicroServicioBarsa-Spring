package com.access.model;

public class DetallePapeleta {
	private Integer Id;
	private String TipoId;
	private Integer Folio;
	private String Codigo;
	private Integer ColorId;
	private Integer Cantidad;
	private Integer ClienteId;
	private Integer Surtida;
	private Integer BackOrder;
	private String Observacion;
	private String NombreCliente;
	private String NombreColor;
	private String DescripcionProducto;
	
	public String getDescripcionProducto() {
		return DescripcionProducto;
	}
	public void setDescripcionProducto(String descripcionProducto) {
		DescripcionProducto = descripcionProducto;
	}
	public String getNombreColor() {
		return NombreColor;
	}
	public void setNombreColor(String nombreColor) {
		NombreColor = nombreColor;
	}
	public String getNombreCliente() {
		return NombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		NombreCliente = nombreCliente;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getTipoId() {
		return TipoId;
	}
	public void setTipoId(String tipoId) {
		TipoId = tipoId;
	}
	public Integer getFolio() {
		return Folio;
	}
	public void setFolio(Integer folio) {
		Folio = folio;
	}
	public String getCodigo() {
		return Codigo;
	}
	public void setCodigo(String codigo) {
		Codigo = codigo;
	}
	public Integer getColorId() {
		return ColorId;
	}
	public void setColorId(Integer colorId) {
		ColorId = colorId;
	}
	public Integer getCantidad() {
		return Cantidad;
	}
	public void setCantidad(Integer cantidad) {
		Cantidad = cantidad;
	}
	public Integer getClienteId() {
		return ClienteId;
	}
	public void setClienteId(Integer clienteId) {
		ClienteId = clienteId;
	}
	public Integer getSurtida() {
		return Surtida;
	}
	public void setSurtida(Integer surtida) {
		Surtida = surtida;
	}
	public Integer getBackOrder() {
		return BackOrder;
	}
	public void setBackOrder(Integer backOrder) {
		BackOrder = backOrder;
	}
	public String getObservacion() {
		return Observacion;
	}
	public void setObservacion(String observacion) {
		Observacion = observacion;
	}
	
		
}
