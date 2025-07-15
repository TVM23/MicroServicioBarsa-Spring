package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.dto.materia.CreateMateriaDTO;
import com.access.model.Imagen;
import com.access.model.Materia;
import com.access.service.BitacoraService;

@Repository
public class MateriaRepository {

	private final JdbcTemplate jdbcTemplate;

	public MateriaRepository(JdbcTemplate jdbcTemplate, BitacoraService bitacoraService) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private Materia convert(ResultSet rs) throws SQLException {
		Materia materia = new Materia();
		materia.setCodigoMat(rs.getString("CodigoMat"));
		materia.setUnidad(rs.getString("Unidad"));
		materia.setDescripcion(rs.getString("Descripcion"));
		materia.setPCompra(rs.getDouble("PCompra"));
		materia.setExistencia(rs.getDouble("Existencia"));
		materia.setMax(rs.getDouble("Max"));
		materia.setMin(rs.getDouble("Min"));
		materia.setInventarioInicial(rs.getDouble("InventarioInicial"));
		materia.setUnidadEntrada(rs.getString("UnidadEntrada"));
		materia.setCantXUnidad(rs.getDouble("CantXUnidad"));
		materia.setProceso(rs.getString("Proceso"));
		materia.setBorrado(rs.getBoolean("Borrado"));
		materia.setMerma(rs.getInt("Merma"));
		materia.setImagenes(this.getImagenesMateria(materia.getCodigoMat()));
		return materia;
	}

	private Imagen convertImg(ResultSet rs) throws SQLException {
		Imagen img = new Imagen();
		img.setId(rs.getInt("Id"));
		img.setCodigoMat(rs.getString("CodigoMat"));
		img.setUrl(rs.getString("ImagenUrl"));
		img.setPublic_id(rs.getString("Public_Id"));
		return img;
	}

	public List<Imagen> getImagenesMateria(String codigo) {
		String sql = "SELECT * FROM ImagenMateria WHERE CodigoMat = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertImg(rs);
		}, codigo);
	}

	public List<Materia> getMateriaByDescripcion(String descripcion) {
		String sql = "SELECT * FROM Materia where Descripcion = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, descripcion);
	}

	public List<Materia> getMateriaByCodigo(String codigo) {
		String sql = "SELECT * FROM Materia where CodigoMat = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, codigo);
	}

	// Este metodo es para que el servicio de las notifs obtenga materia no borradas
	// saber que materias checar y ver si se genera notif o no
	public List<Materia> getMateriasNoBorradas() {
		String sql = "SELECT * FROM Materia where Borrado = false";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		});
	}

	public List<Materia> getMateriasList(String sqlClauses, List<Object> params, int limitValue, int offset) {
		String sql = "SELECT * " + "FROM Materia WHERE 1=1 " + sqlClauses + " LIMIT ? OFFSET ?";
		params.add(limitValue);
		params.add(offset);
		List<Materia> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs); // tu función de conversión
		}, params.toArray());

		return data;
	}

	public int contarElementosMaterias(String sqlClauses, List<Object> params) {
		String countSql = "SELECT COUNT(*) AS total " + "FROM Materia WHERE 1=1 " + sqlClauses;
		int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
		return count;
	}

	public void createNewMateria(CreateMateriaDTO dto) {
		String sql = "INSERT INTO Materia (CodigoMat, Descripcion, Unidad, PCompra, Existencia, Max, Min, "
				+ "InventarioInicial, UnidadEntrada, CantXUnidad, Proceso, Merma, Borrado) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, dto.getCodigoMat(), dto.getDescripcion(), dto.getUnidad(), dto.getPcompra(),
				dto.getExistencia(), dto.getMax(), dto.getMin(), dto.getInventarioInicial(), dto.getUnidadEntrada(),
				dto.getCantxunidad(), dto.getProceso(), dto.getMerma(), dto.getBorrado());
	}

	public void insertImgMateria(String codigoMat, String url, String public_id) {
		String sqlImg = "INSERT INTO ImagenMateria (CodigoMat, ImagenUrl, Public_Id) VALUES (?, ?, ?)";
		jdbcTemplate.update(sqlImg, codigoMat, url, public_id);
	}

	public void updateMateria(CreateMateriaDTO dto) {
		String sql = "UPDATE Materia SET Descripcion = ?, Unidad = ?, PCompra = ?, Existencia = ?, Max = ?, Min = ?, "
				+ "InventarioInicial = ?, UnidadEntrada = ?, CantXUnidad = ?, Proceso = ?, Merma = ?, Borrado = ? "
				+ "WHERE CodigoMat = ? ";
		jdbcTemplate.update(sql, dto.getDescripcion(), dto.getUnidad(), dto.getPcompra(), dto.getExistencia(),
				dto.getMax(), dto.getMin(), dto.getInventarioInicial(), dto.getUnidadEntrada(), dto.getCantxunidad(),
				dto.getProceso(), dto.getMerma(), dto.getBorrado(), dto.getCodigoMat());
	}

	public void logicDeleteMateria(String codigo) {
		String sql = "UPDATE Materia set Borrado = true where CodigoMat = ?";
		jdbcTemplate.update(sql, codigo);
	}

	public void deleteImagenesByCodigoMat(String codigo) {
		String sqlDelete = "DELETE FROM ImagenMateria WHERE CodigoMat = ?";
		jdbcTemplate.update(sqlDelete, codigo);
	}

	public void actualizarExistenciasMateria(Boolean aumenta, Double cantidad, String codigoMat) {
		if (aumenta) {
			jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia + ? WHERE CodigoMat = ?", cantidad,
					codigoMat);
		} else {
			jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia - ? WHERE CodigoMat = ?", cantidad,
					codigoMat);
		}
	}
}
