package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.bitacoras.BitacoraInvListadoDTO;
import com.access.dto.bitacoras.BitacoraProdListadoDTO;
import com.access.dto.inventario.MovimientoProductoPagiDTO;
import com.access.model.Bitacora;
import com.access.model.BitacoraTiempos;
import com.access.model.Movimientos;

@Service
public class BitacoraService {
	private final JdbcTemplate jdbcTemplate;
	
	public BitacoraService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private void insertarMovimiento(String codigo, String movimiento, Boolean aumenta, Double cantidad,
            Double existAnt, Double existNva, Integer colorId) {
			String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
			
			jdbcTemplate.update(sql, fechaActual, codigo, movimiento, aumenta, cantidad, 0, existAnt, existNva, colorId);
	}

	
	public void insertBitacoraRegistro(Integer movId, String tipoMov, Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
		switch (movId) {
			case 1, 3: {
				insertDevolucionFolio(folio, codigo, usuario, cantidad, existAnt, colorId); 
				break;
			}
			case 2: {
				insertDevolucionProv(codigo, usuario, cantidad, existAnt, colorId);
				break; 
			}
			case 4: {
				insertEntradaAlmacen(codigo, usuario, cantidad, existAnt, colorId);
				break;
			}
			case 5: {
				if(tipoMov == "Materia") {
					insertSalidaAlmacen(codigo, usuario, cantidad, existAnt, colorId);
				} else {
					insertSalidaFolio(folio, codigo, usuario, cantidad, existAnt, colorId);
				}
				break;
			}
		}
	}
	
	public void registroInventario(Boolean alta, String codigo, String usuario, Double existNva, Double existAnt, Integer colorId) {
	    java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		if(alta) {
	        insertarMovimiento(codigo, "ALTA", true, existNva, 0.0, existNva, colorId);
		} else {
			Boolean aumenta = existAnt < existNva;
			if(aumenta) {
				cantidad = existNva - existAnt;
			}else {
				cantidad = existAnt - existNva;
			}
	        insertarMovimiento(codigo, "MODIFICACION INVENTARIO: " + usuario.toUpperCase(), aumenta, cantidad, existAnt, existNva, colorId);
		}
	}
	
	//AUMENTAN
	public void insertDevolucionFolio(Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = cantidad + existAnt;
	    insertarMovimiento(codigo, "DEV.FOLIO " + folio + ": " + usuario.toUpperCase(), true, cantidad, existAnt, existNva, colorId);
	}

	public void insertEntradaAlmacen(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = cantidad + existAnt;
	    insertarMovimiento(codigo, "ENTRADA A ALMACEN", true, cantidad, existAnt, existNva, colorId);
	}
	
	
	//NO AUMENTAN
	public void insertDevolucionProv(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "DEV.PROV.: " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}
	
		public void insertSalidaAlmacen(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "SALIDA A ALMACEN", false, cantidad, existAnt, existNva, colorId);
	}
	
	public void insertSalidaFolio(Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "SAL.FOLIO " + folio + ": " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}
	
	
	//////////////////////// METODOS GET /////////////////////////////////////
	///
	
	private Bitacora convertBitInv(ResultSet rs) throws SQLException{
		Bitacora bitInv = new Bitacora();
		bitInv.setId(rs.getInt("Id"));
		bitInv.setFecha(rs.getString("Fecha"));
		bitInv.setCodigo(rs.getString("Codigo"));
		bitInv.setDescripcionCod(rs.getString("DescripcionCod"));
		bitInv.setMovimiento(rs.getString("Movimiento"));
		bitInv.setAumenta(rs.getBoolean("Aumenta"));
		bitInv.setCantidad(rs.getDouble("Cantidad"));
		bitInv.setNoAlmacen(rs.getInt("NoAlmacen"));
		bitInv.setExistAnt(rs.getDouble("ExistAnt"));
		bitInv.setExistNva(rs.getDouble("ExistNva"));
		bitInv.setColorId(rs.getInt("ColorId"));
		bitInv.setDescripcionColor(rs.getString("DescripcionColor"));
		return bitInv;
	}
	
	private BitacoraTiempos convertBitProd(ResultSet rs) throws SQLException{
		BitacoraTiempos bitProd = new BitacoraTiempos();
		bitProd.setId(rs.getInt("Id"));
		bitProd.setFecha(rs.getString("Fecha"));
		bitProd.setFolio(rs.getInt("Folio"));
		bitProd.setEtapa(rs.getString("Etapa"));
		bitProd.setMovimiento(rs.getString("Movimiento"));
		bitProd.setUsuario(rs.getString("Usuario"));
		return bitProd;
	}
	
	
	public PaginationResult<List<Bitacora>> getListadoBitacoraInv(BitacoraInvListadoDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder(
                "FROM Bitacora b " +
                "LEFT JOIN Colores c ON b.ColorId = c.ColorId " +
                "LEFT JOIN Producto p ON b.Codigo = p.Codigo " +
                "LEFT JOIN Materia m ON b.Codigo = m.CodigoMat " +
                "WHERE 1=1 "
        );
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
        String countSql = "SELECT COUNT(*) AS total FROM (SELECT b.Id " + sql.toString() + ") AS conteo";
        int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
        String finalSql = "SELECT b.*, c.Descripcion AS DescripcionColor, COALESCE(p.Descripcion, m.Descripcion) AS DescripcionCod " +
                sql.toString() + " ORDER BY b.Fecha DESC LIMIT ? OFFSET ?";
        params.add(limitValue);
        params.add(offset);

        List<Bitacora> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convertBitInv(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
	public PaginationResult<List<BitacoraTiempos>> getListadoBitacoraProd(BitacoraProdListadoDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
	    StringBuilder sql = new StringBuilder(" FROM BitacoraTiempos WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getId() != null) {
            sql.append(" AND Id = ?");
            params.add(dto.getId());
        }
        
        if (dto.getFolio() != null) {
            sql.append(" AND Folio = ?");
	        params.add(dto.getFolio());
        }
        
        if (dto.getEtapa() != null) {
            sql.append(" AND Etapa = ?");
	        params.add(dto.getEtapa());
        }     
        
        if (dto.getMovimiento() != null) {
            sql.append(" AND Movimiento LIKE ?");
            params.add("%" + dto.getMovimiento() + "%");
        }
        
        if (dto.getUsuario() != null) {
            sql.append(" AND Usuario = ?");
	        params.add(dto.getUsuario());
        }
        
        // Conteo total
	    String countSql = "SELECT COUNT(*) AS total " + sql.toString();
        int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	    String paginatedSql = "SELECT * " + sql.toString() + " LIMIT ? OFFSET ?";
        params.add(limitValue);
        params.add(offset);

        List<BitacoraTiempos> data = jdbcTemplate.query(paginatedSql, (rs, rowNum) -> {
            return convertBitProd(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
}
