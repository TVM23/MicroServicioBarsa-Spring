package com.access.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.bitacoras.BitacoraInvListadoDTO;
import com.access.model.Bitacora;
import com.access.repository.BitacoraRepository;

@Service
public class BitacoraService {
	private final BitacoraRepository bitacoraRepository;
	
	public BitacoraService(BitacoraRepository bitacoraRepository) {
		this.bitacoraRepository = bitacoraRepository;
	}
	
	public void insertBitacoraRegistro(Integer movId, String tipoMov, Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Date fechaActual = Date.valueOf(LocalDate.now());
		switch (movId) {
			case 1, 3: {
				insertDevolucionFolio(folio, fechaActual, codigo, usuario, cantidad, existAnt, colorId); 
				break;
			}
			case 2: {
				insertDevolucionProv(codigo, fechaActual, usuario, cantidad, existAnt, colorId);
				break; 
			}
			case 4: {
				insertEntradaAlmacen(codigo, fechaActual, usuario, cantidad, existAnt, colorId);
				break;
			}
			case 5: {
				if(tipoMov == "Materia") {
					insertSalidaAlmacen(codigo, fechaActual, usuario, cantidad, existAnt, colorId);
				} else {
					insertSalidaFolio(folio, fechaActual, codigo, usuario, cantidad, existAnt, colorId);
				}
				break;
			}
		}
	}
	
	public void registroInventario(Boolean alta, String codigo, String usuario, Double existNva, Double existAnt, Integer colorId) {
	    Double cantidad = 0.0;
	    Date fechaActual = Date.valueOf(LocalDate.now());
		if(alta) {
			bitacoraRepository.insertarMovimiento(codigo, fechaActual, "ALTA", true, existNva, 0.0, existNva, colorId);
		} else {
			Boolean aumenta = existAnt < existNva;
			if(aumenta) {
				cantidad = existNva - existAnt;
			}else {
				cantidad = existAnt - existNva;
			}
			bitacoraRepository.insertarMovimiento(codigo, fechaActual, "MODIFICACION INVENTARIO: " + usuario.toUpperCase(), aumenta, cantidad, existAnt, existNva, colorId);
		}
	}
	
	//AUMENTAN
	public void insertDevolucionFolio(Integer folio, Date fechaActual, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = cantidad + existAnt;
	    bitacoraRepository.insertarMovimiento(codigo, fechaActual, "DEV.FOLIO " + folio + ": " + usuario.toUpperCase(), true, cantidad, existAnt, existNva, colorId);
	}

	public void insertEntradaAlmacen(String codigo, Date fechaActual, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = cantidad + existAnt;
	    bitacoraRepository.insertarMovimiento(codigo, fechaActual, "ENTRADA A ALMACEN", true, cantidad, existAnt, existNva, colorId);
	}
	
	
	//NO AUMENTAN
	public void insertDevolucionProv(String codigo, Date fechaActual, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = existAnt - cantidad;
	    bitacoraRepository.insertarMovimiento(codigo, fechaActual, "DEV.PROV.: " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}
	
		public void insertSalidaAlmacen(String codigo, Date fechaActual, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    bitacoraRepository.insertarMovimiento(codigo, fechaActual, "SALIDA A ALMACEN", false, cantidad, existAnt, existNva, colorId);
	}
	
	public void insertSalidaFolio(Integer folio, Date fechaActual, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    bitacoraRepository.insertarMovimiento(codigo, fechaActual, "SAL.FOLIO " + folio + ": " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}
	
	
	//////////////////////// METODOS GET /////////////////////////////////////
	///
	
	public PaginationResult<List<Bitacora>> getListadoBitacoraInv(BitacoraInvListadoDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND b.Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getId() != null) {
            sql.append(" AND b.Id = ?");
            params.add(dto.getId());
        }

        if (dto.getCodigo() != null) {
            sql.append(" AND LOWER(b.Codigo) LIKE ?");
            params.add("%" + dto.getCodigo().toLowerCase() + "%");
        }

        if (dto.getDescripcionProd() != null) {
            sql.append(" AND LOWER(p.Descripcion) LIKE ?");
            params.add("%" + dto.getDescripcionProd().toLowerCase() + "%");
        }

        if (dto.getDescripcionMat() != null) {
            sql.append(" AND LOWER(m.Descripcion) LIKE ?");
            params.add("%" + dto.getDescripcionMat().toLowerCase() + "%");
        }
        
        
        if (dto.getMovimiento() != null) {
            sql.append(" AND b.Movimiento LIKE ?");
            params.add("%" + dto.getMovimiento() + "%");
        }
        
        if (dto.getAumenta() != null) {
            sql.append(" AND b.Aumenta = ?");
	        params.add(dto.getAumenta());
        }
        
        // Conteo total
        int totalItems = bitacoraRepository.contarElementosBitacoraInv(sql.toString(), params);
        
        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);

        List<Bitacora> data = bitacoraRepository.getBitacoraInventario(sql.toString(), params, limitValue, offset);
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
}
