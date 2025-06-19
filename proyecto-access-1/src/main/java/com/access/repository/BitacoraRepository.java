package com.access.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.Bitacora;

@Repository
public class BitacoraRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public BitacoraRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
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
	
	
	public void insertarMovimiento(String codigo, Date fechaActual, String movimiento, Boolean aumenta, Double cantidad,
            Double existAnt, Double existNva, Integer colorId) {
			String sql = "INSERT INTO Bitacora (Fecha, Codigo, Movimiento, Aumenta, Cantidad, NoAlmacen, ExistAnt, ExistNva, ColorId) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
						jdbcTemplate.update(sql, fechaActual, codigo, movimiento, aumenta, cantidad, 0, existAnt, existNva, colorId);
	}
	
	public List<Bitacora> getBitacoraInventario(String sqlClauses, List<Object> params, int limitValue, int offset){
		String sql = "SELECT b.*, c.Descripcion AS DescripcionColor, COALESCE(p.Descripcion, m.Descripcion) AS DescripcionCod " +
				"FROM Bitacora b " +
                "LEFT JOIN Colores c ON b.ColorId = c.ColorId " +
                "LEFT JOIN Producto p ON b.Codigo = p.Codigo " +
                "LEFT JOIN Materia m ON b.Codigo = m.CodigoMat " +
                "WHERE 1=1 " +
                sqlClauses + " ORDER BY b.Fecha DESC LIMIT ? OFFSET ?";
		params.add(limitValue);
        params.add(offset);
        List<Bitacora> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convertBitInv(rs); // tu función de conversión
        }, params.toArray());
        
        return data;
	}
	
	public int contarElementosBitacoraInv(String sqlClauses, List<Object> params) {
        String countSql = "SELECT COUNT(*) AS total FROM (SELECT b.Id " + 
        		"FROM Bitacora b " +
                "LEFT JOIN Colores c ON b.ColorId = c.ColorId " +
                "LEFT JOIN Producto p ON b.Codigo = p.Codigo " +
                "LEFT JOIN Materia m ON b.Codigo = m.CodigoMat " +
                "WHERE 1=1 " +
                sqlClauses + ") AS conteo";
        int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        return count;
	}
	
}
