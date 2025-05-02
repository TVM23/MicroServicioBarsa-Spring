package com.access.dto.inventario;

import java.util.List;

public class InventarioSalidaDTO {
    private Integer folio;
	private String fecha;
    private String reason;
    private List<InventarioItemDTO> items;
    private String destination;
    private String notes;
    private String createdBy;
    
    public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public int getPapeleta() {
		return folio;
	}
	public void setPapeleta(int papeleta) {
		this.folio = papeleta;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public List<InventarioItemDTO> getItems() {
		return items;
	}
	public void setItems(List<InventarioItemDTO> items) {
		this.items = items;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
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
