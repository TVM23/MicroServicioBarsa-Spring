package com.access.dto.inventario;

import java.util.List;

public class InventarioEntradaDTO {
	private String fecha;
	private Integer proveedorId;
    private List<InventarioItemDTO> items;
	private Double totalAmount;
    private String notes;
    private String createdBy;
    
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Integer getProveedorId() {
		return proveedorId;
	}
	public void setProveedorId(Integer proveedorId) {
		this.proveedorId = proveedorId;
	}
	public List<InventarioItemDTO> getItems() {
		return items;
	}
	public void setItems(List<InventarioItemDTO> items) {
		this.items = items;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
    
    
}
