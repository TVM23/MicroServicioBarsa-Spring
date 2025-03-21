package com.access.dto.producto;

public class ProductoxColorPaginationDTO {
	private Integer page;
	private Integer limit;
	private String codigo;
    private Integer colorId;
    private String desProducto;
    private String desColor;
    
	public String getDesProducto() {
		return desProducto;
	}
	public void setDesProducto(String desProducto) {
		this.desProducto = desProducto;
	}
	public String getDesColor() {
		return desColor;
	}
	public void setDesColor(String desColor) {
		this.desColor = desColor;
	}
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
}
