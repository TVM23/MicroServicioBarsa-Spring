package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.access.model.Proveedor;

@Service
public class ProveedorService {
	private final JdbcTemplate jdbcTemplate;
	
	public ProveedorService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Proveedor convert(ResultSet rs) throws SQLException {
		Proveedor proveedor = new Proveedor();
		proveedor.setProveedorId(rs.getInt("ProveedorId"));
		proveedor.setNombre(rs.getString("Nombre"));
		proveedor.setContacto(rs.getString("Contacto"));
		proveedor.setDir(rs.getString("Dir"));
		proveedor.setCol(rs.getString("Col"));
		proveedor.setTel(rs.getString("Tel"));
		proveedor.setFax(rs.getString("Fax"));
		proveedor.setCd(rs.getString("Cd"));
		proveedor.setCp(rs.getString("CP"));
		proveedor.setRfc(rs.getString("RFC"));
		proveedor.setCurp(rs.getString("Curp"));
		proveedor.setBorrado(rs.getBoolean("Borrado"));
		return proveedor;
	}
		
	public List<Proveedor> getProveedorByID(Integer id) {
        String sql = "SELECT * FROM Proveedores where ProveedorId = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convert(rs);
        }, id);
    }
}
