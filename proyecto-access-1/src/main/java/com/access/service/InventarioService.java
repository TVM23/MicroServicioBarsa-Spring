package com.access.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.access.dto.PaginationResult;
import com.access.dto.inventario.EntradasPaginationDTO;
import com.access.dto.inventario.InventarioEntradaDTO;
import com.access.dto.inventario.InventarioItemDTO;
import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.model.DetallePapeleta;
import com.access.model.InventarioEntrada;
import com.access.model.InventarioEntradaDetalle;
import com.access.model.Materia;
import com.access.model.Papeleta;

@Service
public class InventarioService {
	private final JdbcTemplate jdbcTemplate;
	private final PapeletaService papeletaService;
	private final MateriaService materiaService;
	private final ProveedorService proveedorService;
	
	public InventarioService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService, MateriaService materiaService, ProveedorService proveedorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.papeletaService = papeletaService;
        this.materiaService = materiaService;
        this.proveedorService = proveedorService;
    }
	
	private InventarioEntrada convert(ResultSet rs) throws SQLException {
		InventarioEntrada entrada = new InventarioEntrada();
		entrada.setId(rs.getInt("Id"));
		entrada.setProveedorId(rs.getInt("ProveedorId"));
		entrada.setFecha(rs.getString("Fecha"));
		entrada.setMontoTotal(rs.getDouble("MontoTotal"));
		entrada.setNotas(rs.getString("Notas"));
		entrada.setUsuario(rs.getString("Usuario"));
		entrada.setDetalle(
				getDetalles(entrada.getId())
				);
		return entrada;
	}
	
	private InventarioEntradaDetalle convertDE(ResultSet rs) throws SQLException {
		InventarioEntradaDetalle detalles = new InventarioEntradaDetalle();
		detalles.setId(rs.getInt("Id"));
		detalles.setCantidad(rs.getDouble("Cantidad"));
		detalles.setCodigoMat(rs.getString("CodigoMat"));
		detalles.setDescripcion(rs.getString("Descripcion"));
		detalles.setPCompra(rs.getDouble("PCompra"));
		detalles.setId_Entrada(rs.getInt("Id_Entrada"));
		return detalles;
	}
	
	@Transactional
    public ResponseEntity<?> createSalidaInventario(InventarioSalidaDTO dto) {
        if (papeletaService.getPapeletasByFolio(dto.getFolio()).isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Este folio de papeleta no existe"));
        }

        // Validación de existencia
        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigo() + " no existe"));
            }

            if (materia.get(0).getExistencia() < item.getCantidad()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cantidad insuficiente para la materia con código " + item.getCodigo()));
            }
        }

        // Insertar InventarioSalida
        String sqlSalida = "INSERT INTO Inventario_Salida (Folio, Fecha, Razon, Destino, Notas, Usuario) VALUES (?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getFolio());
            ps.setString(2, dto.getFecha());
            ps.setString(3, dto.getReason());
            ps.setString(4, dto.getDestination());
            ps.setString(5, dto.getNotes());
            ps.setString(6, dto.getCreatedBy());
            return ps;
        }, keyHolder);

        Long idSalida = keyHolder.getKey().longValue();

        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Inventario_Salida (CodigoMat, Cantidad, Id_Salida) VALUES (?, ?, ?)";

        for (InventarioItemDTO item : dto.getItems()) {
            jdbcTemplate.update(sqlDetalle,
                item.getCodigo(),
                item.getCantidad(),
                idSalida
            );

            // Opcional: actualizar existencia en Materia
            jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia - ? WHERE CodigoMat = ?",
                item.getCantidad(),
                item.getCodigo()
            );
        }

        return ResponseEntity.ok(Map.of("message", "Salida de inventario creado exitosamente"));
    }
	
	@Transactional
    public ResponseEntity<?> createEntradaInventario(InventarioEntradaDTO dto) {
        if(proveedorService.getProveedorByID(dto.getProveedorId()).isEmpty()) {
        	return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Este proveedor no existe"));
        }

        // Validación de existencia
        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigo() + " no existe"));
            }
        }

        // Insertar InventarioEntrada
        String sqlSalida = "INSERT INTO Inventario_Entrada (ProveedorId, Fecha, MontoTotal, Notas, Usuario) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getProveedorId());
            ps.setString(2, dto.getFecha());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setString(4, dto.getNotes());
            ps.setString(5, dto.getCreatedBy());
            return ps;
        }, keyHolder);

        Long idSalida = keyHolder.getKey().longValue();

        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Inventario_Entrada (CodigoMat, Cantidad, PCompra, Id_Salida) VALUES (?, ?, ?, ?)";

        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            Double precio = materia.get(0).getPCompra();

            jdbcTemplate.update(sqlDetalle,
                item.getCodigo(),
                item.getCantidad(),
                precio,
                idSalida
            );

            // Opcional: actualizar existencia en Materia
            jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia + ? WHERE CodigoMat = ?",
                item.getCantidad(),
                item.getCodigo()
            );
        }

        return ResponseEntity.ok(Map.of("message", "Entrada de inventario creada exitosamente"));
    }
	
	public PaginationResult<List<InventarioEntrada>> getListadoEntrada(EntradasPaginationDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder("FROM Inventario_Entrada WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getProveedorId() != null) {
            sql.append(" AND ProveedorId = ?");
            params.add(dto.getProveedorId());
        }

        if (dto.getCodigoMat() != null || dto.getDescripcion() != null) {
            sql.append(" AND Id IN (SELECT die.Id_Entrada FROM Detalle_Inventario_Entrada die " +
                       "INNER JOIN Materia m ON die.CodigoMat = m.CodigoMat WHERE 1=1");

            if (dto.getCodigoMat() != null) {
                sql.append(" AND LOWER(die.CodigoMat) LIKE ?");
                params.add("%" + dto.getCodigoMat().toLowerCase() + "%");
            }

            if (dto.getDescripcion() != null) {
                sql.append(" AND LOWER(m.Descripcion) LIKE ?");
                params.add("%" + dto.getDescripcion().toLowerCase() + "%");
            }

            sql.append(")");
        }
        if (dto.getNotes() != null) {
            sql.append(" AND Notas LIKE ?");
            params.add("%" + dto.getNotes() + "%");
        }
        
        if (dto.getUsuario() != null) {
            sql.append(" AND Usuario LIKE ?");
            params.add("%" + dto.getUsuario() + "%");
        }

        
        // Conteo total
        String countSql = "SELECT COUNT(*) AS total " + sql.toString();
        int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
        String finalSql = "SELECT * " + sql.toString() + " LIMIT ? OFFSET ?";
        params.add(limitValue);
        params.add(offset);

        List<InventarioEntrada> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convert(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
	public List<InventarioEntradaDetalle> getDetalles(Integer id){
		String sql = "Select de.*, "
				+ "m.Descripcion AS Descripcion "
				+ "FROM Detalle_Inventario_Entrada de "
				+ "INNER JOIN Materia m ON de.CodigoMat = m.CodigoMat "
				+ "WHERE Id_Entrada = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDE(rs);
		}, id);
	}
}
