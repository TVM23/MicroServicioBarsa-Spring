package com.access.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.access.dto.inventario.DetalleMovProductoDTO;
import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.MovimientosDTO;
import com.access.model.DetalleMovimientoMateria;
import com.access.model.Detalle_Movimiento;
import com.access.model.MovimientoInventario;
import com.access.model.MovimientoMateria;
import com.access.model.Movimientos;

@Repository
public class InventarioRepository {

	private final JdbcTemplate jdbcTemplate;

	public InventarioRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private MovimientoInventario convertMI(ResultSet rs) throws SQLException {
		MovimientoInventario movInv = new MovimientoInventario();
		movInv.setMovId(rs.getInt("MovId"));
		movInv.setDescripcion(rs.getString("Descripcion"));
		movInv.setAumenta(rs.getBoolean("Aumenta"));
		movInv.setBorrado(rs.getBoolean("Borrado"));
		return movInv;
	}

	private MovimientoMateria convertMat(ResultSet rs) throws SQLException {
		MovimientoMateria movMat = new MovimientoMateria();
		List<MovimientoInventario> list = getMovimientoInventarioByMovId(rs.getInt("MovId"));
		String descripcionMov = "";
		if(!list.isEmpty()) {
			descripcionMov = list.get(0).getDescripcion();
		}
		movMat.setConsecutivo(rs.getInt("Consecutivo"));
		movMat.setMovId(rs.getInt("MovId"));
		movMat.setDescripcionInventario(descripcionMov);
		movMat.setFecha(rs.getString("Fecha"));
		movMat.setFolio(rs.getInt("Folio"));
		movMat.setUsuario(rs.getString("Usuario"));
		movMat.setProcesada(rs.getBoolean("Procesada"));
		movMat.setObservacion(rs.getString("Observacion"));
		movMat.setDetalles(getDetallesMovMateria(movMat.getConsecutivo()));
		return movMat;
	}

	private DetalleMovimientoMateria convertDetMat(ResultSet rs) throws SQLException {
		DetalleMovimientoMateria detMovMat = new DetalleMovimientoMateria();
		detMovMat.setId(rs.getInt("Id"));
		detMovMat.setConsecutivo(rs.getInt("Consecutivo"));
		detMovMat.setCodigoMat(rs.getString("CodigoMat"));
		detMovMat.setDescripcion(rs.getString("Descripcion"));
		detMovMat.setCantidad(rs.getDouble("Cantidad"));
		detMovMat.setExistenciaAnterior(rs.getDouble("ExistenciaAnt"));
		detMovMat.setpCosto(rs.getDouble("PCosto"));
		detMovMat.setProcesada(rs.getBoolean("Procesada"));
		return detMovMat;
	}

	private Movimientos convertPro(ResultSet rs) throws SQLException {
		Movimientos movProd = new Movimientos();
		movProd.setConsecutivo(rs.getInt("Consecutivo"));
		movProd.setMovId(rs.getInt("MovId"));
		movProd.setFecha(rs.getString("Fecha"));
		movProd.setFolio(rs.getInt("Folio"));
		movProd.setUsuario(rs.getString("Usuario"));
		movProd.setDetalles(getDetallesMovProd(movProd.getConsecutivo()));
		return movProd;
	}

	private Detalle_Movimiento convertDetPro(ResultSet rs) throws SQLException {
		Detalle_Movimiento detMovPro = new Detalle_Movimiento();
		detMovPro.setConsecutivo(rs.getInt("Consecutivo"));
		detMovPro.setCodigo(rs.getString("Codigo"));
		detMovPro.setColorId(rs.getInt("ColorId"));
		detMovPro.setDescProd(rs.getString("DescProd"));
		detMovPro.setDescColor(rs.getString("DescColor"));
		detMovPro.setCantidad(rs.getInt("Cantidad"));
		detMovPro.setNoAlmacen(rs.getInt("NoAlmacen"));
		return detMovPro;
	}

	public List<MovimientoInventario> getMovimientoInventarioByMovId(Integer movId) {
		String sql = "Select * " + "FROM Movimientos_Inventario " + "WHERE MovId = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertMI(rs);
		}, movId);
	}

	public List<DetalleMovimientoMateria> getDetallesMovMateria(Integer consecutivo) {
		String sql = "Select dm.*, " + "m.Descripcion AS Descripcion " + "FROM Detalle_Movimiento_Materia dm "
				+ "INNER JOIN Materia m ON dm.CodigoMat = m.CodigoMat " + "WHERE Consecutivo = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetMat(rs);
		}, consecutivo);
	}

	public List<Detalle_Movimiento> getDetallesMovProd(Integer consecutivo) {
		String sql = "Select dm.*, " + "p.Descripcion AS DescProd, " + "c.Descripcion AS DescColor "
				+ "FROM Detalle_Movimiento dm " + "INNER JOIN Producto p ON dm.Codigo = p.Codigo "
				+ "INNER JOIN Colores c ON dm.ColorId = c.ColorId " + "WHERE Consecutivo = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetPro(rs);
		}, consecutivo);
	}

	public GeneratedKeyHolder insertMovimientoMateria(MovimientoMateriaDTO dto) {
		String sqlSalida = "INSERT INTO Movimientos_Materia (MovId, Fecha, Folio, Usuario, Procesada, Observacion, Autoriza) VALUES (?, ?, ?, ?, ?, ?, ?)";
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dto.getFecha(), formatter);
		Date sqlDate = java.sql.Date.valueOf(localDate);

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, dto.getMovId());
			ps.setString(2, dto.getFecha());
			// ps.setDate(2, sqlDate);
			ps.setInt(3, dto.getFolio());
			ps.setString(4, dto.getUsuario().toUpperCase());
			ps.setBoolean(5, dto.getProcesada());
			ps.setString(6, dto.getObservacion());
			ps.setString(7, dto.getAutoriza());
			return ps;
		}, keyHolder);

		return keyHolder;
	}

	public void insertDetalleMovimientoMateria(Long consecutivo, String codigoMat, Double cantidad, Double existencia,
			Double pCompra, Boolean procesada) {
		String sqlDetalle = "INSERT INTO Detalle_Movimiento_Materia (Consecutivo, CodigoMat, Cantidad, ExistenciaAnt, "
				+ "PCosto, Procesada) VALUES (?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sqlDetalle, consecutivo, codigoMat, cantidad, existencia, pCompra, procesada);
	}

	public GeneratedKeyHolder insertMovimientoProducto(MovimientosDTO dto) {
		String sqlSalida = "INSERT INTO Movimientos (MovId, Fecha, Folio, Usuario) VALUES (?, ?, ?, ?)";
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dto.getFecha(), formatter);
		Date sqlDate = java.sql.Date.valueOf(localDate);

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, dto.getMovId());
			ps.setString(2, dto.getFecha());
			// ps.setDate(2, sqlDate);
			ps.setInt(3, dto.getFolio());
			ps.setString(4, dto.getUsuario().toUpperCase());
			return ps;
		}, keyHolder);

		return keyHolder;
	}

	public void insertDetalleMovimientoProd(Long consecutivo, DetalleMovProductoDTO item) {
		String sqlDetalle = "INSERT INTO Detalle_Movimiento (Consecutivo, Codigo, ColorId, Cantidad, "
				+ "NoAlmacen) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sqlDetalle, consecutivo, item.getCodigo(), item.getColorId(), item.getCantidad(),
				item.getNoAlmacen());
	}

	public List<MovimientoMateria> getMovimientoMateriaList(String sqlClauses, List<Object> params, int limitValue,
			int offset) {
		String sql = "SELECT * " + "FROM Movimientos_Materia WHERE 1=1 " + sqlClauses  + " ORDER BY Fecha DESC LIMIT ? OFFSET ?";
		params.add(limitValue);
		params.add(offset);
		List<MovimientoMateria> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertMat(rs); // tu función de conversión
		}, params.toArray());

		return data;
	}

	public int contarElementosMovimientoMateria(String sqlClauses, List<Object> params) {
		String countSql = "SELECT COUNT(*) AS total " + "FROM Movimientos_Materia WHERE 1=1 " + sqlClauses;
		int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
		return count;
	}

	public List<Movimientos> getMovimientoProdList(String sqlClauses, List<Object> params, int limitValue, int offset) {
		String sql = "SELECT * " + "FROM Movimientos WHERE 1=1 " + sqlClauses  + " ORDER BY Fecha DESC LIMIT ? OFFSET ?";
		params.add(limitValue);
		params.add(offset);
		List<Movimientos> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertPro(rs); // tu función de conversión
		}, params.toArray());

		return data;
	}

	public int contarElementosMovimientoProd(String sqlClauses, List<Object> params) {
		String countSql = "SELECT COUNT(*) AS total " + "FROM Movimientos WHERE 1=1 " + sqlClauses;
		int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
		return count;
	}
}
