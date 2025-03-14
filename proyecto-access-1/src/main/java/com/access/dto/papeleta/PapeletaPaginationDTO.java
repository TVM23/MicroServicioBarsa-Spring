package com.access.dto.papeleta;

public class PapeletaPaginationDTO {
	
	 private int page;
	 private int limit;
	 private String TipoId;
	 private Integer Folio;
	 private String Fecha;
	 private String Status;
	 private String ObservacionGeneral;
	 
	 
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
	public String getFecha() {
		return Fecha;
	}
	public void setFecha(String fecha) {
		Fecha = fecha;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getObservacionGeneral() {
		return ObservacionGeneral;
	}
	public void setObservacionGeneral(String observacionGeneral) {
		ObservacionGeneral = observacionGeneral;
	}
	 
	 
	    

}
