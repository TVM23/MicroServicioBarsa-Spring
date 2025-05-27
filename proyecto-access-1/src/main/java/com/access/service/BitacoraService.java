package com.access.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
			/*String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        jdbcTemplate.update(sql,
	        		fechaActual,
	        		codigo,
	        		"ALTA",
	        		true,
	        		existNva,
	        		0,
	        		existAnt,
	        		existNva,
	        		colorId
	        		);*/
	        insertarMovimiento(codigo, "ALTA", true, existNva, 0.0, existNva, colorId);
		} else {
			Boolean aumenta = existAnt < existNva;
			if(aumenta) {
				cantidad = existNva - existAnt;
			}else {
				cantidad = existAnt - existNva;
			}
	        insertarMovimiento(codigo, "MODIFICACION INVENTARIO: " + usuario.toUpperCase(), aumenta, cantidad, existAnt, existNva, colorId);

			/*String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        jdbcTemplate.update(sql,
	        		fechaActual,
	        		codigo,
	        		"MODIFICACION INVENTARIO: "+usuario.toUpperCase(),
	        		aumenta,
	        		cantidad,
	        		0,
	        		existAnt,
	        		existNva,
	        		colorId
	        		); */
		}
	}
	
	public void insertDevolucionProv(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "DEV.PROV.: " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}
	
	/*public void insertDevolucionProv(String codigo, String usuario, Double existNva, Double existAnt,  Integer colorId) {
		Boolean movimiento = false;
		java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		cantidad = existAnt - existNva;
		String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
        		fechaActual,
        		codigo,
        		"DEV.PROV.: " + usuario.toUpperCase(),
        		movimiento,
        		cantidad,
        		0,
        		existAnt,
        		existNva,
        		colorId
        		);
	}*/
	
	public void insertDevolucionFolio(Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = cantidad + existAnt;
	    insertarMovimiento(codigo, "DEV.FOLIO " + folio + ": " + usuario.toUpperCase(), true, cantidad, existAnt, existNva, colorId);
	}
	
	/*public void insertDevolucionFolio(String folio, String codigo, String usuario, Double existNva, Double existAnt,  Integer colorId) {
		Boolean movimiento = true;
		java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		cantidad = existNva + existAnt;
		String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
        		fechaActual,
        		codigo,
        		"DEVOLUCION FOLIO: "+folio+". "+ usuario.toUpperCase(),
        		movimiento,
        		cantidad,
        		0,
        		existAnt,
        		existNva,
        		colorId
        		);
	}*/
	
	public void insertEntradaAlmacen(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    Double existNva = cantidad + existAnt;
	    insertarMovimiento(codigo, "ENTRADA A ALMACEN", true, cantidad, existAnt, existNva, colorId);
	}
	
	/*public void insertEntradaAlmacen(String codigo, String usuario, Double existNva, Double existAnt, Integer colorId) {
		Boolean movimiento = true;
		java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		cantidad = existNva + existAnt;
		String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
        		fechaActual,
        		codigo,
        		"ENTRADA A ALMACEN",
        		movimiento,
        		cantidad,
        		0,
        		existAnt,
        		existNva,
        		colorId
        		);
	}*/
	
	public void insertSalidaAlmacen(String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "SALIDA A ALMACEN", false, cantidad, existAnt, existNva, colorId);
	}
	
	/*public void insertSalidaAlmacen(String codigo, String usuario, Double existNva, Double existAnt,  Integer colorId) {
		Boolean movimiento = false;
		java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		cantidad = existAnt - existNva;
		String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
        		fechaActual,
        		codigo,
        		"SALIDA DE ALMACEN",
        		movimiento,
        		cantidad,
        		0,
        		existAnt,
        		existNva,
        		colorId
        		);
	}*/
	
	public void insertSalidaFolio(Integer folio, String codigo, String usuario, Double cantidad, Double existAnt, Integer colorId) {
	    double existNva = existAnt - cantidad;
	    insertarMovimiento(codigo, "SAL.FOLIO " + folio + ": " + usuario.toUpperCase(), false, cantidad, existAnt, existNva, colorId);
	}

	
	/*public void insertSalidaFolio(String folio, String codigo, String usuario, Double existNva, Double existAnt,  Integer colorId) {
		Boolean movimiento = false;
		java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
	    Double cantidad = 0.0;
		cantidad = existAnt - existNva;
		String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
        		fechaActual,
        		codigo,
        		"SALIDA FOLIO: "+folio+". "+ usuario.toUpperCase(),
        		movimiento,
        		cantidad,
        		0,
        		existAnt,
        		existNva,
        		colorId
        		);
	}*/
}
