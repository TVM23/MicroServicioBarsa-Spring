package com.access.model;

import java.util.List;

public class Papeleta {
    
    private String TipoId;
    private Integer Folio;
    private String Fecha;
    private String Status;
    private String ObservacionGeneral;
    private List<DetallePapeleta> Detallepapeleta;

	public Papeleta() {}
    
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
	
	public List<DetallePapeleta> getDetallepapeleta() {
		return Detallepapeleta;
	}

	public void setDetallepapeleta(List<DetallePapeleta> detallepapeleta) {
		Detallepapeleta = detallepapeleta;
	}

}