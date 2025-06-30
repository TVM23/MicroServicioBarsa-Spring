package com.access.dto.produccion;

public class PausarTiempoDTO {
	private Integer folio;
	private String etapa;
	private Integer tiempo;
	private String nombreUsuario;
	private String fechaPausa;

	public String getFechaPausa() {
		return fechaPausa;
	}
	public void setFechaPausa(String fechaPausa) {
		this.fechaPausa = fechaPausa;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public Integer getTiempo() {
		return tiempo;
	}
	public void setTiempo(Integer tiempo) {
		this.tiempo = tiempo;
	}
	
}
