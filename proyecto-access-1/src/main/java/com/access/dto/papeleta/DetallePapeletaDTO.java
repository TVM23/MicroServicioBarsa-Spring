package com.access.dto.papeleta;

import com.access.model.DetallePapeleta;

public class DetallePapeletaDTO {
    private DetallePapeleta detallePapeleta;
    private String nombreCliente;

    public DetallePapeletaDTO(DetallePapeleta detallePapeleta, String nombreCliente) {
        this.detallePapeleta = detallePapeleta;
        this.nombreCliente = nombreCliente;
    }

    public DetallePapeleta getDetallePapeleta() {
        return detallePapeleta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}
