package com.access.dto.produccion;

public class ReiniciarTiempoDTO {
	private String etapa;
	private Integer folio;
	private String nombreUsuario;
	private String fechaPausa;

	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public String getFechaPausa() {
		return fechaPausa;
	}
	public void setFechaPausa(String fechaPausa) {
		this.fechaPausa = fechaPausa;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
}
