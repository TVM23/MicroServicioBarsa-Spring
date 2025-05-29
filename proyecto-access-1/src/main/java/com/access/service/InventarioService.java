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
import com.access.dto.inventario.DetalleMovProductoDTO;
import com.access.dto.inventario.EntradasPaginationDTO;
import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.MovimientoMateriaPagiDto;
import com.access.dto.inventario.MovimientoProductoPagiDTO;
import com.access.dto.inventario.MovimientosDTO;
import com.access.dto.inventario.SalidaPaginationDTO;
import com.access.model.DetalleMovimientoMateria;
import com.access.model.Detalle_Movimiento;
import com.access.model.Materia;
import com.access.model.MovimientoInventario;
import com.access.model.MovimientoMateria;
import com.access.model.Movimientos;
import com.access.model.Producto_X_Color;

@Service
public class InventarioService {
	private final JdbcTemplate jdbcTemplate;
	private final PapeletaService papeletaService;
	private final MateriaService materiaService;
	private final ProveedorService proveedorService;
	private final Prod_x_ColorService prod_x_ColorService;
	private final BitacoraService bitacoraservice;
	private final NotificacionService notificacionService;
	
	public InventarioService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService, MateriaService materiaService, ProveedorService proveedorService,
			Prod_x_ColorService prod_x_ColorService, BitacoraService bitacoraService, NotificacionService notificacionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.papeletaService = papeletaService;
        this.materiaService = materiaService;
        this.proveedorService = proveedorService;
        this.prod_x_ColorService = prod_x_ColorService;
        this.bitacoraservice = bitacoraService;;
        this.notificacionService = notificacionService;
    }
	
	private MovimientoInventario convertMI(ResultSet rs) throws SQLException {
		MovimientoInventario movInv = new MovimientoInventario();
		movInv.setMovId(rs.getInt("MovId"));
		movInv.setDescripcion(rs.getString("Descripcion"));
		movInv.setAumenta(rs.getBoolean("Aumenta"));
		movInv.setBorrado(rs.getBoolean("Borrado"));
		return movInv;
	}
	
	public List<MovimientoInventario> getMovimientoInventarioByMovId(Integer movId){
		String sql = "Select * "
				+ "FROM Movimientos_Inventario "
				+ "WHERE MovId = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertMI(rs);
		}, movId);
	}
	
	
	private MovimientoMateria convertMat(ResultSet rs) throws SQLException{
		MovimientoMateria movMat = new MovimientoMateria();
		List<MovimientoInventario> list = getMovimientoInventarioByMovId(rs.getInt("MovId"));
		String descripcionMov = list.get(0).getDescripcion();
		movMat.setConsecutivo(rs.getInt("Consecutivo"));
		movMat.setMovId(rs.getInt("MovId"));
		movMat.setDescripcionInventario(descripcionMov);
		movMat.setFecha(rs.getString("Fecha"));
		movMat.setFolio(rs.getInt("Folio"));
		movMat.setUsuario(rs.getString("Usuario"));
		movMat.setProcesada(rs.getBoolean("Procesada"));
		movMat.setObservacion(rs.getString("Observacion"));
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
		detMovMat.setExistenciaAnterior(rs.getDouble("ExistenciaAnt"));
		detMovMat.setpCosto(rs.getDouble("PCosto"));
		detMovMat.setProcesada(rs.getBoolean("Procesada"));
		return detMovMat;
	}
	
	private Movimientos convertPro(ResultSet rs) throws SQLException{
		Movimientos movProd = new Movimientos();
		movProd.setConsecutivo(rs.getInt("Consecutivo"));
		movProd.setMovId(rs.getInt("MovId"));
		movProd.setFecha(rs.getString("Fecha"));
		movProd.setFolio(rs.getInt("Folio"));
		movProd.setUsuario(rs.getString("Usuario"));
		movProd.setDetalles(
				getDetallesMovProd(movProd.getConsecutivo())
				);
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
	
	public List<Detalle_Movimiento> getDetallesMovProd(Integer consecutivo){
		String sql = "Select dm.*, "
				+ "p.Descripcion AS DescProd, "
				+ "c.Descripcion AS DescColor "
				+ "FROM Detalle_Movimiento dm "
				+ "INNER JOIN Producto p ON dm.Codigo = p.Codigo "
				+ "INNER JOIN Colores c ON dm.ColorId = c.ColorId "
				+ "WHERE Consecutivo = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetPro(rs);
		}, consecutivo);
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
            ps.setString(4, dto.getUsuario().toUpperCase());
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
                        
            bitacoraservice.insertBitacoraRegistro(dto.getMovId(), "Materia", dto.getFolio(), item.getCodigoMat(), dto.getUsuario().toUpperCase(), 
            		item.getCantidad(), materia.get(0).getExistencia(), null);
            
	        notificacionService.evaluarNotificacion(item.getCodigoMat());
            
        }

        return ResponseEntity.ok(Map.of("message", "Movimiento de materia generado exitosamente"));
    }
	
	@Transactional
    public ResponseEntity<?> createMovimientoProducto(MovimientosDTO dto) {
        // Validación de existencia
        for (DetalleMovProductoDTO item : dto.getDetalles()) {
            List<Producto_X_Color> prod = prod_x_ColorService.getDetallesProductoXColor(item.getCodigo(), item.getColorId());
            if (prod.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No existe el producto con el codigo "+item.getCodigo()+" o con el Id color "+item.getColorId()));
            }

            /*if (materia.get(0).getExistencia() < item.getCantidad()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cantidad insuficiente para la materia con código " + item.getCodigo()));
            }*/
        }		
        // Insertar InventarioSalida
        String sqlSalida = "INSERT INTO Movimientos (MovId, Fecha, Folio, Usuario) VALUES (?, ?, ?, ?)";
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
            ps.setString(4, dto.getUsuario().toUpperCase());
            return ps;
        }, keyHolder);

        Long consecutivo = keyHolder.getKey().longValue();
             
        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Movimiento (Consecutivo, Codigo, ColorId, Cantidad, "
        		+ "NoAlmacen) VALUES (?, ?, ?, ?, ?)";
        
        List<MovimientoInventario> movimiento = getMovimientoInventarioByMovId(dto.getMovId());
        if(movimiento.isEmpty()) {
        	return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Este tipo de movimiento de inventario no existe"));
        }
        Boolean aumenta = movimiento.get(0).getAumenta();        

        for (DetalleMovProductoDTO item : dto.getDetalles()) {
        	
            List<Producto_X_Color> prod = prod_x_ColorService.getDetallesProductoXColor(item.getCodigo(), item.getColorId());
            jdbcTemplate.update(sqlDetalle,
            	consecutivo,
                item.getCodigo(),
                item.getColorId(),
                item.getCantidad(),
                item.getNoAlmacen()
            );
            
            // Actualizar existencia en Materia
            if(aumenta) {
            	jdbcTemplate.update("UPDATE Producto_X_Color SET Existencia = Existencia + ? WHERE Codigo = ? AND ColorId = ?",
                        item.getCantidad(),
                        item.getCodigo(),
                        item.getColorId()
                    );
            }else {
            	jdbcTemplate.update("UPDATE Producto_X_Color SET Existencia = Existencia - ? WHERE Codigo = ? AND ColorId = ?",
                        item.getCantidad(),
                        item.getCodigo(),
                        item.getColorId()
                    );
            }
            
            bitacoraservice.insertBitacoraRegistro(dto.getMovId(), "Producto", dto.getFolio(), item.getCodigo(), dto.getUsuario().toUpperCase(), 
            		item.getCantidad().doubleValue(), prod.get(0).getExistencia().doubleValue(), item.getColorId());
                        
        }

        return ResponseEntity.ok(Map.of("message", "Movimiento de productos generado exitosamente"));
    }
	
	public PaginationResult<List<MovimientoMateria>> getListadoMovMateria(MovimientoMateriaPagiDto dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder("FROM Movimientos_Materia WHERE 1=1");
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
            sql.append(" AND Consecutivo IN (SELECT dmm.Consecutivo FROM Detalle_Movimiento_Materia dmm " +
                       "INNER JOIN Materia m ON dmm.CodigoMat = m.CodigoMat WHERE 1=1");

            if (dto.getCodigoMat() != null) {
                sql.append(" AND LOWER(dmm.CodigoMat) LIKE ?");
                params.add("%" + dto.getCodigoMat().toLowerCase() + "%");
            }

            if (dto.getDescripcion() != null) {
                sql.append(" AND LOWER(m.Descripcion) LIKE ?");
                params.add("%" + dto.getDescripcion().toLowerCase() + "%");
            }

            sql.append(")");
        }
        
        if (dto.getMovId() != null) {
            sql.append(" AND MovId = ?");
            params.add(dto.getMovId());
        }
        
        if (dto.getConsecutivo() != null) {
            sql.append(" AND Consecutivo = ?");
            params.add(dto.getConsecutivo());
        }
        
        if (dto.getObservacion() != null) {
            sql.append(" AND Observacion LIKE ?");
            params.add("%" + dto.getObservacion() + "%");
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

        List<MovimientoMateria> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convertMat(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
	
	public PaginationResult<List<Movimientos>> getListadoMovProducto(MovimientoProductoPagiDTO dto){
		int pageValue = dto.getPage();
        int limitValue = dto.getLimit();
        int offset = (pageValue - 1) * limitValue;
                
        StringBuilder sql = new StringBuilder("FROM Movimientos WHERE 1=1");
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

        if (dto.getCodigo() != null || dto.getDescripcion() != null) {
            sql.append(" AND Consecutivo IN (SELECT dmp.Consecutivo FROM Detalle_Movimiento dmp " +
                       "INNER JOIN Producto p ON dmp.Codigo = p.Codigo WHERE 1=1");

            if (dto.getCodigo() != null) {
                sql.append(" AND LOWER(dmp.Codigo) LIKE ?");
                params.add("%" + dto.getCodigo().toLowerCase() + "%");
            }

            if (dto.getDescripcion() != null) {
                sql.append(" AND LOWER(p.Descripcion) LIKE ?");
                params.add("%" + dto.getDescripcion().toLowerCase() + "%");
            }

            sql.append(")");
        }
        
        if (dto.getMovId() != null) {
            sql.append(" AND MovId = ?");
            params.add(dto.getMovId());
        }
        
        if (dto.getConsecutivo() != null) {
            sql.append(" AND Consecutivo = ?");
            params.add(dto.getConsecutivo());
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

        List<Movimientos> data = jdbcTemplate.query(finalSql, (rs, rowNum) -> {
            return convertPro(rs); // tu función de conversión
        }, params.toArray());
        
        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
}
