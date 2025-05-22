package com.access.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.access.dto.inventario.DetalleMovMateriaDTO;
import com.access.dto.inventario.EntradasPaginationDTO;
import com.access.dto.inventario.InventarioEntradaDTO;
import com.access.dto.inventario.InventarioItemDTO;
import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.SalidaPaginationDTO;
import com.access.model.DetalleMovimientoMateria;
import com.access.model.InventarioEntrada;
import com.access.model.InventarioEntradaDetalle;
import com.access.model.InventarioSalida;
import com.access.model.InventarioSalidaDetalle;
import com.access.model.Materia;
import com.access.model.MovimientoInventario;
import com.access.model.MovimientoMateria;

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
	
	private InventarioEntrada convertEntrada(ResultSet rs) throws SQLException {
		InventarioEntrada entrada = new InventarioEntrada();
		entrada.setId(rs.getInt("Id"));
		entrada.setProveedorId(rs.getInt("ProveedorId"));
		entrada.setProveedorNombre(rs.getString("ProveedorNombre"));
		entrada.setFecha(rs.getString("Fecha"));
		entrada.setMontoTotal(rs.getDouble("MontoTotal"));
		entrada.setNotas(rs.getString("Notas"));
		entrada.setUsuario(rs.getString("Usuario"));
		entrada.setDetalle(
				getDetallesEntrada(entrada.getId())
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
	
	private InventarioSalida convertSalida(ResultSet rs) throws SQLException {
		InventarioSalida salida = new InventarioSalida();
		salida.setId(rs.getInt("Id"));
		salida.setFolio(rs.getInt("Folio"));
		salida.setFecha(rs.getString("Fecha"));
		salida.setRazon(rs.getString("Razon"));
		salida.setDestino(rs.getString("Destino"));
		salida.setNotas(rs.getString("Notas"));
		salida.setUsuario(rs.getString("Usuario"));
		salida.setDetalle(
				getDetallesSalida(salida.getId())
				);
		return salida;
	}
	
	private InventarioSalidaDetalle convertDS(ResultSet rs) throws SQLException {
		InventarioSalidaDetalle detalles = new InventarioSalidaDetalle();
		detalles.setId(rs.getInt("Id"));
		detalles.setCantidad(rs.getDouble("Cantidad"));
		detalles.setCodigoMat(rs.getString("CodigoMat"));
		detalles.setDescripcion(rs.getString("Descripcion"));
		detalles.setId_Salida(rs.getInt("Id_Salida"));
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
		System.out.println("Checo el proveedor");

        // Validación de existencia
        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigo() + " no existe"));
            }
        }
		System.out.println("Checo si los materiales existen");


        // Insertar InventarioEntrada
        String sqlSalida = "INSERT INTO Inventario_Entrada (ProveedorId, Fecha, MontoTotal, Notas, Usuario) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		System.out.println("Hizo la cadena de texto insercion en la tabla del InventarioEntrada");


        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getProveedorId());
            ps.setString(2, dto.getFecha());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setString(4, dto.getNotes());
            ps.setString(5, dto.getCreatedBy());
            return ps;
        }, keyHolder);
		System.out.println("Hizo la insercion en la tabla del InventarioEntrada");


        Long idSalida = keyHolder.getKey().longValue();

        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Inventario_Entrada (CodigoMat, Cantidad, PCompra, Id_Entrada) VALUES (?, ?, ?, ?)";

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
                
        StringBuilder sql = new StringBuilder("FROM Inventario_Entrada ie INNER JOIN Proveedores p ON ie.ProveedorId = p.ProveedorId WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND ie.Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getProveedorId() != null) {
            sql.append(" AND ie.ProveedorId = ?");
            params.add(dto.getProveedorId());
        }
        
        if(dto.getProveedorNombre() != null) {
        	sql.append(" AND p.Nombre LIKE ? ");
            params.add("%" + dto.getProveedorNombre() + "%");
        }

        if (dto.getCodigoMat() != null || dto.getDescripcion() != null) {
            sql.append(" AND ie.Id IN (SELECT die.Id_Entrada FROM Detalle_Inventario_Entrada die " +
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
            sql.append(" AND ie.Notas LIKE ?");
            params.add("%" + dto.getNotes() + "%");
        }
        
        if (dto.getUsuario() != null) {
            sql.append(" AND ie.Usuario LIKE ?");
            params.add("%" + dto.getUsuario() + "%");
        }

        
        // Conteo total
        String countSql = "SELECT COUNT(*) AS total " + sql.toString();
        int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        // Paginación
        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
        String finalSql = "SELECT ie.*, p.Nombre AS ProveedorNombre " + sql.toString() + " LIMIT ? OFFSET ?";
        params.add(limitValue);
        params.add(offset);

        List<InventarioEntrada> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convertEntrada(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
	public List<InventarioEntradaDetalle> getDetallesEntrada(Integer id){
		String sql = "Select de.*, "
				+ "m.Descripcion AS Descripcion "
				+ "FROM Detalle_Inventario_Entrada de "
				+ "INNER JOIN Materia m ON de.CodigoMat = m.CodigoMat "
				+ "WHERE Id_Entrada = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDE(rs);
		}, id);
	}
	
	
	public PaginationResult<List<InventarioSalida>> getListadoSalida(SalidaPaginationDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder("FROM Inventario_Salida WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
            sql.append(" AND Fecha BETWEEN ? AND ?");
            params.add(dto.getFechaInicio());
            params.add(dto.getFechaFin());
        }

        if (dto.getFolio() != null) {
            sql.append(" AND Folio = ?");
            params.add(dto.getFolio());
        }

        if (dto.getCodigoMat() != null || dto.getDescripcion() != null) {
            sql.append(" AND Id IN (SELECT die.Id_Salida FROM Detalle_Inventario_Salida die " +
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
        
        if (dto.getRazon() != null) {
            sql.append(" AND Razon LIKE ?");
            params.add("%" + dto.getRazon() + "%");
        }
        
        if (dto.getDestino() != null) {
            sql.append(" AND Destino LIKE ?");
            params.add("%" + dto.getDestino() + "%");
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

        List<InventarioSalida> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convertSalida(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
	
	public List<InventarioSalidaDetalle> getDetallesSalida(Integer id){
		String sql = "Select ds.*, "
				+ "m.Descripcion AS Descripcion "
				+ "FROM Detalle_Inventario_Salida ds "
				+ "INNER JOIN Materia m ON ds.CodigoMat = m.CodigoMat "
				+ "WHERE Id_Salida = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDS(rs);
		}, id);
	}
	
	
	
	
	
	
	private MovimientoMateria convertMat(ResultSet rs) throws SQLException{
		MovimientoMateria movMat = new MovimientoMateria();
		movMat.setConsecutivo(rs.getInt("Consecutivo"));
		movMat.setMovId(rs.getInt("MovId"));
		movMat.setFecha(rs.getString("Fecha"));
		movMat.setFolio(rs.getInt("Folio"));
		movMat.setUsuario(rs.getString("Usuario"));
		movMat.setProcesada(rs.getBoolean("Procesada"));
		movMat.setDetalles(
				getDetallesMovMateria(movMat.getConsecutivo())
				);
		return movMat;
	}
	
	private DetalleMovimientoMateria convertDetMat(ResultSet rs) throws SQLException {
		DetalleMovimientoMateria detMovMat = new DetalleMovimientoMateria();
		detMovMat.setId(rs.getInt("Id"));
		detMovMat.setConsecutivo(rs.getInt("Consecutivo"));
		detMovMat.setCodigoMat(rs.getString("CodigoMat"));
		detMovMat.setDescripcion(rs.getString("Descripcion"));
		detMovMat.setCantidad(rs.getDouble("Cantidad"));
		detMovMat.setExistenciaAnterior(rs.getDouble("ExistenciaAnterior"));
		detMovMat.setpCosto(rs.getDouble("PCosto"));
		detMovMat.setProcesada(rs.getBoolean("Procesada"));
		return detMovMat;
	}
	
	private MovimientoInventario convertMI(ResultSet rs) throws SQLException {
		MovimientoInventario movInv = new MovimientoInventario();
		movInv.setMovId(rs.getInt("MovId"));
		movInv.setDescripcion(rs.getString("Descripcion"));
		movInv.setAumenta(rs.getBoolean("Aumenta"));
		movInv.setBorrado(rs.getBoolean("Borrado"));
		return movInv;
	}
	
	public List<DetalleMovimientoMateria> getDetallesMovMateria(Integer consecutivo){
		String sql = "Select dm.*, "
				+ "m.Descripcion AS Descripcion "
				+ "FROM Detalle_Movimiento_Materia dm "
				+ "INNER JOIN Materia m ON dm.CodigoMat = m.CodigoMat "
				+ "WHERE Consecutivo = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetMat(rs);
		}, consecutivo);
	}
	
	public List<MovimientoInventario> getMovimientoInventarioByMovId(Integer movId){
		String sql = "Select * "
				+ "FROM Movimientos_Inventario "
				+ "WHERE MovId = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertMI(rs);
		}, movId);
	}
	
	@Transactional
    public ResponseEntity<?> createMovimientoMateria(MovimientoMateriaDTO dto) {
		if(dto.getFolio() != 0) {
			if (papeletaService.getPapeletasByFolio(dto.getFolio()).isEmpty()) {
	            return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("error", "Este folio de papeleta no existe"));
	        }
		}

        // Validación de existencia
        for (DetalleMovMateriaDTO item : dto.getDetalles()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigoMat());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigoMat() + " no existe"));
            }

            /*if (materia.get(0).getExistencia() < item.getCantidad()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cantidad insuficiente para la materia con código " + item.getCodigo()));
            }*/
        }

        // Insertar InventarioSalida
        String sqlSalida = "INSERT INTO Movimientos_Materia (MovId, Fecha, Folio, Usuario, Procesada, Observacion, Autoriza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dto.getFecha(), formatter);
        Date sqlDate = java.sql.Date.valueOf(localDate);  


        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getMovId());
            ps.setString(2, dto.getFecha());
            //ps.setDate(2, sqlDate);
            ps.setInt(3, dto.getFolio());
            ps.setString(4, dto.getUsuario());
            ps.setBoolean(5, dto.getProcesada());
            ps.setString(6, dto.getObservacion());
            ps.setString(7, dto.getAutoriza());
            return ps;
        }, keyHolder);

        Long consecutivo = keyHolder.getKey().longValue();
     
        // Insertar detalles

        String sqlDetalle = "INSERT INTO Detalle_Movimiento_Materia (Consecutivo, CodigoMat, Cantidad, ExistenciaAnt, "
        		+ "PCosto, Procesada) VALUES (?, ?, ?, ?, ?, ?)";
        
        List<MovimientoInventario> movimiento = getMovimientoInventarioByMovId(dto.getMovId());
        if(movimiento.isEmpty()) {
        	return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Este tipo de movimiento de inventario no existe"));
        }
        Boolean aumenta = movimiento.get(0).getAumenta();        

        for (DetalleMovMateriaDTO item : dto.getDetalles()) {

            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigoMat());
            jdbcTemplate.update(sqlDetalle,
            	consecutivo,
                item.getCodigoMat(),
                item.getCantidad(),
                materia.get(0).getExistencia(),
                materia.get(0).getPCompra(),
                item.getProcesada()
            );
            
            // Actualizar existencia en Materia
            if(aumenta) {
            	jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia + ? WHERE CodigoMat = ?",
                        item.getCantidad(),
                        item.getCodigoMat()
                    );
            }else {
            	jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia - ? WHERE CodigoMat = ?",
                        item.getCantidad(),
                        item.getCodigoMat()
                    );
            }
        }

        return ResponseEntity.ok(Map.of("message", "Movimiento de materia generado exitosamente"));
    }
}
